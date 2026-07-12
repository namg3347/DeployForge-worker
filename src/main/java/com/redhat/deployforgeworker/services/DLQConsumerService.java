package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.enums.DeploymentStatus;
import com.redhat.deployforgeworker.models.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DLQConsumerService {

    private final DeploymentService deploymentService;

    //@RabbitListener(queues = "dead_letter_queue")
    public void consumeMessage(String message) {
        log.info("found message in dead letter queue:{}",message);
        Long id = Long.parseLong(message.split(":")[1]);
        Deployment deployment = deploymentService.findDeploymentById(id);
        log.info("found deployment:{}",deployment);
        deploymentService.updateDeploymentStatus(deployment, DeploymentStatus.FAILED);
        log.info("deployment failed completely");

    }
}
