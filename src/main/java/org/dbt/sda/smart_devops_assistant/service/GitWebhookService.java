package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GitWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(GitWebhookService.class);

    AIService aiService;

    GitService gitService;

    SlackService slackService;
    PopulateVectorStore populateVectorStore;

    public GitWebhookService(AIService aiService,
                             GitService gitService,
                             SlackService slackService,
                             PopulateVectorStore populateVectorStore) {
        this.aiService = aiService;
        this.gitService = gitService;
        this.slackService = slackService;
        this.populateVectorStore = populateVectorStore;
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

            ResponseEntity<List<GitChangedFile>> response = gitService.fetchPRFiles(request.pullRequest().number().toString());
            if(response.getStatusCode().is2xxSuccessful()){
                List<GitChangedFile> changedFiles = response.getBody();

                if(Objects.nonNull(changedFiles) && !changedFiles.isEmpty()) {
                    logger.info("Start loading Vector Store with changed files...");
                    long start = System.currentTimeMillis();
                    populateVectorStore.populateVectorStore(changedFiles);
                    logger.info("Vector store loading completes in {}ms !!", (System.currentTimeMillis()-start));
                } else {
                    return ResponseEntity.noContent().build();
                }
            }

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
