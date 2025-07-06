package org.dbt.sda.smart_devops_assistant.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PRSummaryRequest{

    private String title;
    private String description;
    @JsonProperty(value = "pr_url")
    private String prUrl;
    private String diff;

    @Override
    public String toString() {
        return "PRSummaryRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", prUrl='" + prUrl + '\'' +
                ", diff='" + diff + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrUrl() {
        return prUrl;
    }

    public void setPrUrl(String prUrl) {
        this.prUrl = prUrl;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }
}
