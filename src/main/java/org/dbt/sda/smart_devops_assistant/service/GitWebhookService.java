package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.entities.PRSuggestionResponse;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryRequest;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryResponse;
import org.dbt.sda.smart_devops_assistant.entities.WebhookRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class GitWebhookService {

    AIService aiService;

    RestTemplate restTemplate;

    public GitWebhookService(AIService aiService, RestTemplateBuilder restTemplateBuilder) {
        this.aiService = aiService;
        this.restTemplate = restTemplateBuilder.build();
    }

    public PRSuggestionResponse analyzePR(WebhookRequest request) {
        if (Objects.nonNull(request.pullRequest()) && request.pullRequest().number() > 0) {
            System.out.println("Request Data:"
                    + request.pullRequest().number() + "--"
                    + request.pullRequest().url() + "--"
                    + request.pullRequest().state());

            String prDiffStr = restTemplate.getForObject(request.pullRequest().diffUrl(), String.class);

            return aiService.analyzePR(prDiffStr);
        }
        return null;
    }

    public PRSummaryResponse generateSummary(PRSummaryRequest prSummaryRequest) {
        if (Objects.nonNull(prSummaryRequest.getPrUrl())) {
            System.out.println("Request Data:" + prSummaryRequest.getPrUrl());

            String prDiffStr = restTemplate.getForObject(prSummaryRequest.getPrUrl()+".diff", String.class);
            prSummaryRequest.setDiff(prDiffStr);
            return aiService.generatePRSummary(prSummaryRequest);
        }
        return null;
    }
}
