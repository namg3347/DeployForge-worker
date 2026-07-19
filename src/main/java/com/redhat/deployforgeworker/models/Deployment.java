package com.redhat.deployforgeworker.models;

import com.redhat.deployforgeworker.enums.DeploymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Table(name = "deployments",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "repoName"}
                )
        })
public class Deployment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dpl_seq_gen")
    @SequenceGenerator(name = "dpl_seq_gen", sequenceName = "dpls_dpl_id_seq", allocationSize = 1)
    private Long deploymentId;

    @Column(name="user_id",nullable = false,updatable = false)
    private Long userId;

    @Column(name = "repo_name",nullable = false,updatable = false)
    private String projectName;

    @Column(name = "repo_url",nullable = false,updatable = false)
    private String repoUrl;

    @Column(name = "deployment_slug",unique = true,updatable = false)
    private String deploymentSlug;

    @Column(name = "build_command",nullable = false,updatable = false)
    private String buildCommand;

    @Column(name = "output_directory",nullable = false,updatable = false)
    private String outputDirectory;

    @Column(name = "created_at",updatable = false)
    private Instant createdAt;

    @Column(name = "started_at",updatable = false)
    private Instant startedAt;

    @Column(name = "deployed_at",updatable = false)
    private Instant deployedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "deployment_status", nullable = false)
    private DeploymentStatus deploymentStatus;

    @Column(name = "error_message")
    private String errorMessage;



}
