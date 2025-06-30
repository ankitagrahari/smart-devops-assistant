package org.dbt.sda.smart_devops_assistant.entities;

public record PullRequest (
    String url,
    String htmlUrl,
    String diffUrl,
    String patchUrl,
    String state,
    Integer number,
    String body
) {}
