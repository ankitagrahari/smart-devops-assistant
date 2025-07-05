package org.dbt.sda.smart_devops_assistant.controllers;

import org.dbt.sda.smart_devops_assistant.entities.PRSuggestionResponse;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryRequest;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryResponse;
import org.dbt.sda.smart_devops_assistant.entities.WebhookRequest;
import org.dbt.sda.smart_devops_assistant.service.GitWebhookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitWebhookController {

    GitWebhookService service;

    public GitWebhookController(GitWebhookService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String allOK(){
        return "Working...OK!!";
    }

    @PostMapping("/pr-analyze")
    public PRSuggestionResponse analyzePR(@RequestBody WebhookRequest request){
        System.out.println("request:"+ request);
        return service.analyzePR(request);
    }

    @PostMapping("/generate-summary")
    public PRSummaryResponse generateSummary(@RequestBody PRSummaryRequest prSummaryRequest){
        return service.generateSummary(prSummaryRequest);
    }
}
