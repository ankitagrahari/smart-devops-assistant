package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.entities.PRSummaryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SlackService {

    @Value("${slack.incoming.webhook.url}")
    private String SLACK_WEBHOOK_INCOMING_URL;

    private RestTemplate restTemplate;

    public SlackService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void sendPRReviewToSlack(PRSummaryResponse prSummaryResponse){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String payload = "{\"text\":\""+prSummaryResponse.summary()+"\"}";
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(SLACK_WEBHOOK_INCOMING_URL, entity, String.class);
    }
}
