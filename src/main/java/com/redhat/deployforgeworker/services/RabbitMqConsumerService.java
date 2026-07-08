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

    @RabbitListener(queues = "deploy_forge_queue")
    public Deployment consumeMessage(String message) {
        log.info("found message:{}",message);
        // message is deploymentId:{id}
        Long id = Long.parseLong(message.split(":")[1]);
        return deploymentService.findDeploymentById(id);
    }

}
