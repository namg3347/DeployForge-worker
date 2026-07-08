package com.redhat.deployforgeworker.repositories;

import com.redhat.deployforgeworker.models.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentRepo extends JpaRepository<Deployment, Long> {
}
