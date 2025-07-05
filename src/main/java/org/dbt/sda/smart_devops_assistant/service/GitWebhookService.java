package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.entities.PRSuggestionResponse;
import org.dbt.sda.smart_devops_assistant.entities.WebhookRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.stream.Collectors;

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
            System.out.println("prDiff:"+ prDiffStr);

            PRSuggestionResponse response = aiService.analyzePR(prDiffStr);
//            System.out.println("Response:"+response);
            return response;
        }
        return null;
    }
}
