package com.redhat.deployforgeworker.services;


import com.redhat.deployforgeworker.models.Deployment;

public interface ProcessBuilderService {

    void createTemporaryDirectory(Long deploymentId);

    Deployment runBuilderContainer(Long deploymentId);

    void deleteTemporaryDirectory(Long deploymentId);
}
