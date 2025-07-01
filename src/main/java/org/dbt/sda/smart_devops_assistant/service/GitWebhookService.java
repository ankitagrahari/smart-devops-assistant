package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.entities.WebhookRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GitWebhookService {

    public String analyzePR(WebhookRequest request){
        if(Objects.nonNull(request.pullRequest()) && request.pullRequest().number()>0){
            return request.pullRequest().number()
                    + "--"
                    + request.pullRequest().url()
                    + "--"
                    + request.pullRequest().state();
        }
        return "";
    }
}
