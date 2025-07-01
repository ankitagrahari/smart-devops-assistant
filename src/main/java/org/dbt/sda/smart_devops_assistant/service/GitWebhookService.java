package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.entities.WebhookRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GitWebhookService {

    AIService aiService;

    public GitWebhookService(AIService aiService) {
        this.aiService = aiService;
    }

    public String analyzePR(WebhookRequest request) {
        if (Objects.nonNull(request.pullRequest()) && request.pullRequest().number() > 0) {
            System.out.println("Request Data:"
                    + request.pullRequest().number() + "--"
                    + request.pullRequest().url() + "--"
                    + request.pullRequest().state());

            String response = aiService.analyzePR(request.pullRequest().url());
            System.out.println("Response:"+response);
            return response;
        }
        return "";
    }
}
