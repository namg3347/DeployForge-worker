package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.models.Deployment;

public interface ProcessBuilderService {

    void createTemporaryDirectory(Long deploymentID);

    void runBuilderContainer(Deployment deployment);

    void deleteTemporaryDirectory(Long deploymentID);
}
