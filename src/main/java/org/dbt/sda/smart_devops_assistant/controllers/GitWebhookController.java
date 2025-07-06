package org.dbt.sda.smart_devops_assistant.controllers;

import org.dbt.sda.smart_devops_assistant.entities.PRSuggestionResponse;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryRequest;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryResponse;
import org.dbt.sda.smart_devops_assistant.entities.WebhookRequest;
import org.dbt.sda.smart_devops_assistant.service.GitWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(GitWebhookController.class);

    GitWebhookService service;

    public GitWebhookController(GitWebhookService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String allOK(){
        return "Working...OK!!";
    }

    @PostMapping("/pr-analyze")
    public ResponseEntity<PRSuggestionResponse> analyzePR(@RequestBody WebhookRequest request){
        logger.debug("request:{}", request);
        return service.analyzePR(request);
    }

    @PostMapping("/generate-summary")
    public ResponseEntity<PRSummaryResponse> generateSummary(@RequestBody PRSummaryRequest prSummaryRequest){
        return service.generateSummary(prSummaryRequest);
    }
}
