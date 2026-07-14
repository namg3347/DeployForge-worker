package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.models.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMqConsumerService {

    private final DeploymentService deploymentService;
    private final WorkerService workerService;

    //@RabbitListener(queues = "deploy_forge_queue")
    public void consumeMessage(String message) {
        log.info("found message:{}",message);
        Long id = Long.parseLong(message.split(":")[1]);
        Deployment deployment = deploymentService.findDeploymentById(id);
        log.info("found deployment:{}",deployment);
        workerService.run(deployment.getDeploymentId());

        log.info("deployment successfully completed");

    }

}
