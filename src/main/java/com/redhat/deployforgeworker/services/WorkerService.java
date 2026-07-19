package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.exceptions.DomainException;
import com.redhat.deployforgeworker.models.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerService {

    private final DeploymentService deploymentService;
    private final ProcessBuilderService processBuilderService;
    private final S3Service s3Service;

    public void run(Long deploymentId) {

        try {

            //BUILDING-----------------
            log.info("started building deployment:");
            //mark deployment as Building
            deploymentService.markBuilding(deploymentId);

            //creates temporary dir
            processBuilderService.createTemporaryDirectory(deploymentId);

            //runs a container for our container
            Deployment deployment = processBuilderService.runBuilderContainer(deploymentId);

            log.info("finished building deployment");


            //UPLOADING-----------------

            log.info("started uploading deployment");
            //mark deployment as uploading
            deploymentService.markUploading(deploymentId);

            //Call to upload service
            s3Service.uploadOutputDirectory(deployment);

            log.info("finished uploading deployment");

            //mark deployment as successful
            deploymentService.markSuccess(deploymentId);

            log.info("deployment SUCCESSFUL!");
        } catch (DomainException e) {
            log.error("deployment failed:{} with code:{}", e.getMessage(),e.getStatusCode());
            deploymentService.setErrorMessage(deploymentId, e.getMessage());
        } finally {
            //deletes temporary dir
            try {
                processBuilderService.deleteTemporaryDirectory(deploymentId);
            } catch (Exception e) {
                log.error("cleanup failed : {}", e.getMessage());
            }
        }


    }
}
