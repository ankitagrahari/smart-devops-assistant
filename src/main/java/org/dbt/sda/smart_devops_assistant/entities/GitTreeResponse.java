package org.dbt.sda.smart_devops_assistant.entities;

import java.util.List;

public record GitTreeResponse(
        String sha,
        String url,
        List<GitFileMetaData> tree,
        boolean truncated
) {
}
