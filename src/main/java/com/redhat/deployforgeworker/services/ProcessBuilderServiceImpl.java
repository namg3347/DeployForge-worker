package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.enums.DeploymentsLogsLevel;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessBuilderServiceImpl implements ProcessBuilderService {

    private final DeploymentService deploymentService;
    private final DeploymentsLogsService deploymentsLogsService;


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
    public Deployment runBuilderContainer(Long deploymentId) {
        log.info("Running builder container for deployment with id:{}", deploymentId);
        ProcessBuilder processBuilder = new  ProcessBuilder();
        Deployment deployment =deploymentService.findDeploymentById(deploymentId);
        List<String> command = getDockerCommand(deployment);
        try {
            log.info("builder reached try block");
            processBuilder.command(command);
            //redirects error stream to output stream
            processBuilder.redirectErrorStream(true);
            // inherits IO to print logs directly to console

            // for dev --------------
            //processBuilder.inheritIO();

            Process process = processBuilder.start();
            log.info("builder process started");

            // for prod --------------
            //save logs to db
            saveLogs(process,deployment.getDeploymentId());

            log.info("waiting for builder container to finish");
            int exitCode = process.waitFor();
            if(exitCode!=0){
                log.info("Docker process exited with code:{} ", exitCode);
                throw  new BuilderContainerException("Error while running builder container");
            }

            return deployment;

        } catch ( IOException | InterruptedException e ) {
            throw new BuilderContainerException("failed to run docker container, error:"+e.getMessage());

        }
    }

    @Override
    public void deleteTemporaryDirectory(Long deploymentId) {
        Path path = Paths.get(System.getProperty("java.io.tmpdir"),"build-" +deploymentId);
        AtomicBoolean failed = new AtomicBoolean(false);
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
                            failed.set(true);
                            log.error("failed to delete item,{} : {}",p,e.getMessage());
                        }
                    });
            if(failed.get()){
                throw new TempDirException("Failed to delete some files inside temporary directory");
            }
            log.info("Successfully deleted temporary directory: {}",path.toAbsolutePath());
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

    private void saveLogs(Process process, Long deploymentId) throws LoggingException {
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            String line;
            long sequence = 1;

            while ((line = reader.readLine()) != null) {

                log.info(line);

                DeploymentsLogsLevel level;
                String message;

                if (line.startsWith("[INFO]")) {
                    level = DeploymentsLogsLevel.INFO;
                    message = line.replaceFirst("\\[INFO]\\s*", "");

                } else if (line.startsWith("[ERROR]")) {
                    level = DeploymentsLogsLevel.ERROR;
                    message = line.replaceFirst("\\[ERROR]\\s*", "");

                } else {
                    level = DeploymentsLogsLevel.RAW;
                    message = line;
                }

                deploymentsLogsService.saveLogs(
                        deploymentId,
                        message,
                        sequence++,
                        level
                );
            }

        } catch (IOException e) {
            throw new LoggingException("Error while saving builder logs.");
        }
    }


}
