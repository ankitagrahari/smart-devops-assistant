package org.dbt.sda.smart_devops_assistant.service;

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

    public String analyzePR(String prURL){
        PromptTemplate pt = new PromptTemplate("""
            summarize the following pull request diff and suggest improvements {prURL}
        """);

        String response = chatClient
                .prompt(pt.create(Map.of("prURL", prURL)))
                .call()
                .content();

        System.out.println("Response:"+ response);
        return response;
    }
}
