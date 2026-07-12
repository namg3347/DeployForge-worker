package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.enums.DeploymentStatus;
import com.redhat.deployforgeworker.models.Deployment;

public interface DeploymentService {

    Deployment findDeploymentById(Long id);

    void setStartedAt(Deployment deployment);

    void setDeployedAt(Deployment deployment);

    void updateDeploymentStatus(Deployment deployment, DeploymentStatus deploymentStatus);

    void setErrorMessage(Deployment deployment, String errorMessage);
}
