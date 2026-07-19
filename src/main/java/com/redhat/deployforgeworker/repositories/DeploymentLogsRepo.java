package com.redhat.deployforgeworker.repositories;

import com.redhat.deployforgeworker.models.DeploymentsLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentLogsRepo extends JpaRepository<DeploymentsLogs, Long> {
}
