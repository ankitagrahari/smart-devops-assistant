package org.dbt.sda.smart_devops_assistant.entities;

public record Repository(
        String name,
        String fullName,
        String description,
        Integer openIssues
) {}
