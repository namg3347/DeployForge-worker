package com.redhat.deployforgeworker.services;


public interface ProcessBuilderService {

    void createTemporaryDirectory(Long deploymentId);

    void runBuilderContainer(Long deploymentId);

    void deleteTemporaryDirectory(Long deploymentId);
}
