package org.dbt.sda.smart_devops_assistant.entities;

import java.util.List;

public class PRSuggestionResponse {

    private String summary;
    private List<String> suggestions;
    private String testCaseIdea;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public String getTestCaseIdea() {
        return testCaseIdea;
    }

    public void setTestCaseIdea(String testCaseIdea) {
        this.testCaseIdea = testCaseIdea;
    }
}
