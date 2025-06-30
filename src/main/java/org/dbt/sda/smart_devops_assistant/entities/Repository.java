package org.dbt.sda.smart_devops_assistant.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Repository(
        String name,
        @JsonProperty(value = "full_name")
        String fullName,
        String description,
        @JsonProperty(value = "open_issues")
        Integer openIssues
) {}
