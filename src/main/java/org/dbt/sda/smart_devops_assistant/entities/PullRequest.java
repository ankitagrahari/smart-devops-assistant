package org.dbt.sda.smart_devops_assistant.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PullRequest (
    String url,
    @JsonProperty(value = "html_url")
    String htmlUrl,
    @JsonProperty(value = "diff_url")
    String diffUrl,
    @JsonProperty(value = "patch_url")
    String patchUrl,
    String state,
    Integer number,
    String body
) {}
