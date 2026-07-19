package com.redhat.deployforgeworker.models;

import com.redhat.deployforgeworker.enums.DeploymentsLogsLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Table(name = "deployments_logs")
public class DeploymentsLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dpl_log_seq_gen")
    @SequenceGenerator(name = "dpl_log_seq_gen", sequenceName = "dpls_dpl_log_id_seq", allocationSize = 1)
    private Long id;

    @Column(name ="deployment_id" ,nullable = false,updatable = false)
    private Long deploymentId;

    @Column(name = "time_stamp",updatable = false,nullable = false)
    @CreationTimestamp
    private Instant timestamp;

    @Column(name = "sequence_number",nullable = false,updatable = false)
    private Long sequenceNumber;

    @Enumerated(EnumType.STRING)
    private DeploymentsLogsLevel level;

    @Column(name = "message",nullable = false,updatable = false)
    private String message;

}
