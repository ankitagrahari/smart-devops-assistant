package org.dbt.sda.smart_devops_assistant.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitChangedFile (
        String sha,
        String filename,
        String status,
        int additions,
        int deletions,
        int changes,
        @JsonProperty("raw_url")
        String rawURL,
        @JsonProperty("content_url")
        String contentURL,
        String patch
){}
