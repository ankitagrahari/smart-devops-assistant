package org.dbt.sda.smart_devops_assistant.entities;

public record WebhookRequest (
    String action,
    Integer number,
    PullRequest pullRequest,
    Repository repository
){}
