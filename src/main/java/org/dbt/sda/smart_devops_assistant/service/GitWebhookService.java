package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.entities.PRSuggestionResponse;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryRequest;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryResponse;
import org.dbt.sda.smart_devops_assistant.entities.WebhookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GitWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(GitWebhookService.class);

    AIService aiService;

    GitService gitService;

    SlackService slackService;

    public GitWebhookService(AIService aiService, GitService gitService, SlackService slackService) {
        this.aiService = aiService;
        this.gitService = gitService;
        this.slackService = slackService;
    }

    public ResponseEntity<PRSuggestionResponse> analyzePR(WebhookRequest request) {
        if (Objects.nonNull(request.pullRequest()) && request.pullRequest().number() > 0) {
            logger.debug("Request Data:{}--{}--{}", request.pullRequest().number(), request.pullRequest().url(), request.pullRequest().state());

            ResponseEntity<String> prDiffResponse = gitService.fetchPRDiff(request.pullRequest().diffUrl());
            String prDiffStr = "";
            if(prDiffResponse.getStatusCode().is2xxSuccessful())
                prDiffStr = prDiffResponse.getBody();
            else
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok(aiService.analyzePR(prDiffStr));
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<PRSummaryResponse> generateSummary(PRSummaryRequest prSummaryRequest) {
        if (Objects.nonNull(prSummaryRequest.getPrUrl())) {
            logger.debug("Request Data:{}", prSummaryRequest.getPrUrl());

            ResponseEntity<String> prDiffResponse = gitService.fetchPRDiff(prSummaryRequest.getPrUrl()+".diff");
            String prDiffStr = "";
            if(prDiffResponse.getStatusCode().is2xxSuccessful())
                prDiffStr = prDiffResponse.getBody();
            else
                return ResponseEntity.notFound().build();

            prSummaryRequest.setDiff(prDiffStr);

            PRSummaryResponse response = aiService.generatePRSummary(prSummaryRequest);

            //Send the response to Slack channel
            //TODO: Add PR Url and the Author of the PR also to the request.
            slackService.sendPRReviewToSlack(response);

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }
}
