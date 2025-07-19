package org.dbt.sda.smart_devops_assistant.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitFileContent(
        String sha,
        @JsonProperty("node_id")
        String nodeId,
        String url,
        String content,
        String encoding,
        Long size
) {
}
