# smart-devops-assistant

is a project on exploring AI agent in our day to day usage. 
- It will accept PR metadata from github webhook and send the data to AI agent
  - Endpoint: `/pr-analyze` -- send PR diff or link, get LLM analysis
  - Endpoint: `/generate-summary` -- PR summary
- An AI assistant which will handles the response formatting and security
- Accept input text and call AI agents like OpenAI or Ollama with prompts like
`summarize the following pull request diff and suggest improvements`
- return structured JSON output with summary, suggestions and optional test case idea. 