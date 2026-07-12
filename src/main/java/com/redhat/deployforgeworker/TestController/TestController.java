package com.redhat.deployforgeworker.TestController;

import com.redhat.deployforgeworker.models.Deployment;
import com.redhat.deployforgeworker.services.DeploymentService;
import com.redhat.deployforgeworker.services.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final WorkerService workerService;
    private final DeploymentService deploymentService;
    @GetMapping("/{id}")
     public String test(@PathVariable Long id) {
        Deployment deployment = deploymentService.findDeploymentById(id);
        workerService.run(deployment);

        return "success";
    }
}
