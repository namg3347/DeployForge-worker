package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.enums.DeploymentsLogsLevel;
import com.redhat.deployforgeworker.models.DeploymentsLogs;
import com.redhat.deployforgeworker.repositories.DeploymentLogsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeploymentsLogsService {
    private final DeploymentLogsRepo deploymentLogsRepo;

    public void saveLogs(Long deploymentId,
                         String message,
                         Long sequence,
                         DeploymentsLogsLevel level) {

        DeploymentsLogs logs = DeploymentsLogs.builder()
                .deploymentId(deploymentId)
                .sequenceNumber(sequence)
                .level(level)
                .message(message)
                .build();
        deploymentLogsRepo.save(logs);

    }
}
