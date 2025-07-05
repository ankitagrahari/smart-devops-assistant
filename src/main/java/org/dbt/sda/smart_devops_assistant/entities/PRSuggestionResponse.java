package org.dbt.sda.smart_devops_assistant.entities;

public class PRSuggestionResponse {

    private String summary;
    private String suggestions;
    private String testCaseIdea;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getTestCaseIdea() {
        return testCaseIdea;
    }

    public void setTestCaseIdea(String testCaseIdea) {
        this.testCaseIdea = testCaseIdea;
    }
}
