package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.enums.DeploymentStatus;
import com.redhat.deployforgeworker.exceptions.DeploymentNotFoundException;
import com.redhat.deployforgeworker.models.Deployment;
import com.redhat.deployforgeworker.repositories.DeploymentRepo;
import lombok.Data;
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
    public void updateDeploymentStatus(Deployment deployment, DeploymentStatus deploymentStatus) {
        deployment.setDeploymentStatus(deploymentStatus);
        deploymentRepo.save(deployment);
    }
}
