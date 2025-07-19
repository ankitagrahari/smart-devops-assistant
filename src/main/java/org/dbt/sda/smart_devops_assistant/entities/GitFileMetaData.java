package org.dbt.sda.smart_devops_assistant.entities;

public record GitFileMetaData(
        String path,
        String mode,
        String type,
        String sha,
        int size,
        String url
) {}
