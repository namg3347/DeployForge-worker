package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.exceptions.BuilderContainerException;
import com.redhat.deployforgeworker.exceptions.LoggingException;
import com.redhat.deployforgeworker.exceptions.TempDirException;
import com.redhat.deployforgeworker.models.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessBuilderServiceImpl implements ProcessBuilderService {

    @Override
    public void createTemporaryDirectory(Long deploymentId) {
        try {
            log.info("Creating temporary directory for deployment with id:{}", deploymentId);

            /* defines a path safely in system's temporary directory
            where our builder container will store the output directory*/
            Path path = Paths.get(System.getProperty("java.io.tmpdir"),
                    "build-" + deploymentId);
            /* this creates a directory inside the path defined*/
            Path dir = Files.createDirectories(path);
            log.info("Temporary directory successfully ready at: {}", dir.toAbsolutePath());
        } catch (IOException  e) {
            log.error("Failed to create temp directory for deployment {}: {}",
                    deploymentId, e.getMessage());
            throw new TempDirException("Error while creating temporary directory");
        }
    }

    @Override
    public void runBuilderContainer(Deployment deployment) {
        log.info("Running builder container for deployment with id:{}", deployment.getDeploymentId());
        ProcessBuilder processBuilder = new  ProcessBuilder();
        List<String> command = getDockerCommand(deployment);
        try {
            processBuilder.command(command);
            //redirects error stream to output stream
            processBuilder.redirectErrorStream(true);
            // inherits IO to print logs directly to console

            // for dev --------------
            processBuilder.inheritIO();

            Process process = processBuilder.start();

            // for prod --------------
            //save logs to db
            //saveLogs(process);

            int exitCode = process.waitFor();
            if(exitCode!=0){
                throw  new BuilderContainerException("Error while running builder container");
            }
            log.info("Docker process exited with code:{} ", exitCode);

        } catch ( IOException | InterruptedException e ) {
            throw new BuilderContainerException("failed to run docker container, error:"+e.getMessage());

        } catch ( LoggingException e ) {
            throw new LoggingException(e.getMessage());
        }
    }

    @Override
    public void deleteTemporaryDirectory(Long deploymentId) {
        Path path = Paths.get(System.getProperty("java.io.tmpdir"),"build-" +deploymentId);

        log.info("Deleting temporary directory for deployment with id:{}", deploymentId);
        if(!Files.exists(path)){
            log.warn("Temporary Directory does not exist...skipping deletion");
            return;
        }
        try(Stream<Path> walk  = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(p-> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            log.error("failed to delete item,{} : {}",p,e.getMessage());
                        }
                    });
            log.info("Successfully deleted temporary directory for deployment: {}", deploymentId);
        } catch (IOException e) {
            log.error("Failed to read directory tree for deployment {}: {}", deploymentId, e.getMessage());
            throw new TempDirException("Error while deleting temporary directory");
        }
    }


    private static List<String> getDockerCommand(Deployment deployment) {
        Path path = Paths.get(System.getProperty("java.io.tmpdir"),
                "build-" + deployment.getDeploymentId()).toAbsolutePath();
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");
        command.add("--rm");
        command.add("--name");
        command.add("builder-"+deployment.getDeploymentId());
        command.add("-v");
        command.add(path+":/workspace");
        command.add("-e");
        command.add("REPO_URL=" + deployment.getRepoUrl());
        command.add("-e");
        command.add("BUILD_COMMAND=" + deployment.getBuildCommand());
        command.add("-e");
        command.add("OUTPUT_DIR=" + deployment.getOutputDirectory());
        command.add("deployforge-builder");
        return command;
    }

    private static void saveLogs(Process process) throws LoggingException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while((line = reader.readLine())!= null) {
                log.info(line);

                //save to db------

            }

        } catch (IOException e) {
            throw new LoggingException("Error while saving logs to builder container");
        }
    }


}
