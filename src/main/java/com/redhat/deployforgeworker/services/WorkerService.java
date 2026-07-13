package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.enums.DeploymentStatus;
import com.redhat.deployforgeworker.models.Deployment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerService {

    private final DeploymentService deploymentService;
    private final ProcessBuilderService processBuilderService;

    public void run(Deployment deployment) {

        try {

            //BUILDING-----------------
            log.info("started building deployment:");
            //update deployment status to Building
            deploymentService.updateDeploymentStatus(deployment, DeploymentStatus.BUILDING);
            //set started at instant
            deploymentService.setStartedAt(deployment);

            //creates temporary dir
            processBuilderService.createTemporaryDirectory(deployment.getDeploymentId());

            //runs a container for our container
            processBuilderService.runBuilderContainer(deployment);

            log.info("finished building deployment");


            //UPLOADING-----------------

            log.info("started uploading deployment");
            deploymentService.updateDeploymentStatus(deployment,DeploymentStatus.UPLOADING);

            //Call to upload service

            log.info("finished uploading deployment");


            deploymentService.setDeployedAt(deployment);
            deploymentService.updateDeploymentStatus(deployment,DeploymentStatus.SUCCESS);

            log.info("deployment SUCCESSFUL!");
        } catch (Exception e) {
            log.error("deployment failed", e);
            deploymentService.setErrorMessage(deployment, e.getMessage());
        } finally {
            //deletes temporary dir
            processBuilderService.deleteTemporaryDirectory(deployment.getDeploymentId());
        }


    }
}
