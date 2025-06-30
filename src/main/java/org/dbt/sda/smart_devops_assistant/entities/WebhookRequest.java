package org.dbt.sda.smart_devops_assistant.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WebhookRequest (
    String action,
    Integer number,
    @JsonProperty(value="pull_request")
    PullRequest pullRequest,
    Repository repository
){}
