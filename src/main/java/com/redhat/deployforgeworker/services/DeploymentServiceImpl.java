package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.enums.DeploymentStatus;
import com.redhat.deployforgeworker.exceptions.DeploymentNotFoundException;
import com.redhat.deployforgeworker.models.Deployment;
import com.redhat.deployforgeworker.repositories.DeploymentRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentServiceImpl implements DeploymentService {

    private final DeploymentRepo deploymentRepo;

    @Override
    public Deployment findDeploymentById(Long id) {
        Deployment deployment = deploymentRepo.findById(id).orElse(null);

        if(deployment == null) {
            throw new DeploymentNotFoundException("Deployment with id " + id + " not found");
        }

        return deployment;
    }

    @Override
    @Transactional
    public void markBuilding(Long deploymentId) {
        Deployment deployment = findDeploymentById(deploymentId);

        deployment.setDeploymentStatus(DeploymentStatus.BUILDING);
        deployment.setStartedAt(Instant.now());
    }

    @Override
    @Transactional
    public void markUploading(Long deploymentId) {
        Deployment deployment = findDeploymentById(deploymentId);

        deployment.setDeploymentStatus(DeploymentStatus.UPLOADING);
    }

    @Override
    @Transactional
    public void markSuccess(Long deploymentId) {
        Deployment deployment = findDeploymentById(deploymentId);

        deployment.setDeploymentStatus(DeploymentStatus.SUCCESS);
        deployment.setDeployedAt(Instant.now());
    }

    @Override
    @Transactional
    public void markFailure(Long deploymentId) {
        Deployment deployment = findDeploymentById(deploymentId);

        deployment.setDeploymentStatus(DeploymentStatus.FAILED);
    }

    @Override
    @Transactional
    public void setErrorMessage(Long deploymentId, String errorMessage) {
        Deployment deployment = findDeploymentById(deploymentId);
        deployment.setErrorMessage(errorMessage);
    }

}
