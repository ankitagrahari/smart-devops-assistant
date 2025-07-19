package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.entities.PRSuggestionResponse;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryRequest;
import org.dbt.sda.smart_devops_assistant.entities.PRSummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final ChatClient chatClient;

    VectorStore vectorStore;

    private static final Double SIMILARITY_THRESHOLD = 0.7;

    public AIService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.vectorStore = vectorStore;
    }

    public PRSuggestionResponse analyzePR(String prDiff){
        PromptTemplate pt = new PromptTemplate("""
            As an expert programmer and given the following context from the codebase and this PR diff {prDiff},
            review the pull request difference and suggest improvements.
        """);

        QuestionAnswerAdvisor qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(SIMILARITY_THRESHOLD)
                        .query("The files mentioned in the PR diff to provide the context for improvements")
                        .topK(3)
                        .build())
                .build();

        PRSuggestionResponse response = chatClient
                .prompt(pt.create(Map.of("prDiff", prDiff)))
                .advisors(qaAdvisor)
                .call()
                .entity(PRSuggestionResponse.class);

        logger.debug("Response:{}", response);
        return response;
    }

    public PRSummaryResponse generatePRSummary(PRSummaryRequest prSummaryRequest){
        PromptTemplate pt = new PromptTemplate("""
            Given the following PR title {title}, description {description} and optional difference {diff}, generate a clear 1-2 sentence summary
            describing what this PR does ?
        """);
        return chatClient
                .prompt(pt.create(Map.of("title", prSummaryRequest.getTitle(),
                        "description", prSummaryRequest.getDescription(),
                        "diff", prSummaryRequest.getDiff())))
                .call()
                .entity(PRSummaryResponse.class);
    }
}
