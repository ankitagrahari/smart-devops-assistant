package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.entities.PRSuggestionResponse;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryRequest;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
public class AIService {

    private final ChatClient chatClient;

    public AIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public PRSuggestionResponse analyzePR(String prDiffURL){
        PromptTemplate pt = new PromptTemplate("""
            As an expert programmer, review the following pull request difference and suggest improvements {prDiffURL}
        """);

        PRSuggestionResponse response = chatClient
                .prompt(pt.create(Map.of("prDiffURL", prDiffURL)))
                .call()
                .entity(PRSuggestionResponse.class);

        System.out.println("Response:"+ response);
        return response;
    }

    public PRSummaryResponse generatePRSummary(PRSummaryRequest prSummaryRequest){
        PromptTemplate pt = new PromptTemplate("""
            Given the following PR title {title}, description {description} and optional difference {diff}, generate a clear 1-2 sentence summary
            describing what this PR does?
        """);
        return chatClient
                .prompt(pt.create(Map.of("title", prSummaryRequest.getTitle(),
                        "description", prSummaryRequest.getDescription(),
                        "diff", prSummaryRequest.getDiff())))
                .call()
                .entity(PRSummaryResponse.class);
    }
}
