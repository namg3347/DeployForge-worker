package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.enums.DeploymentStatus;
import com.redhat.deployforgeworker.models.Deployment;

public interface DeploymentService {

    Deployment findDeploymentById(Long id);

    void updateDeploymentStatus(Deployment deployment, DeploymentStatus deploymentStatus);
}
