package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.models.Deployment;

public interface DeploymentService {

    Deployment findDeploymentById(Long id);

    void markBuilding(Long deploymentId);

    void markUploading(Long deploymentId);

    void markSuccess(Long deploymentId);

    void markFailure(Long deploymentId);


    void setErrorMessage(Long deploymentID, String errorMessage);
}
