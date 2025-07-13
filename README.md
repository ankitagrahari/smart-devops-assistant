# smart-devops-assistant
This is an AI assistant which will help developers review the pull request and 
provide suggestion on the code changes. It will also push the review messages to Slack.

The **development process** is divided into 4 phases and 1 option phase. 
- [x] Phase 1: Foundation and Setup (Build base Project and integrate basic Github PR analysis) 
- [x] Phase 2: Integrate Github + Slack (Create a working CI workflow that uses AI for code review summaries)
- [ ] Phase 3: RAG with Vector Store (Make AI context-aware using your actual codebase/docs)
- [ ] Phase 4: AI Agents & Coordination (Use multi-agents for dedicated tasks)
- [ ] Phase 5: Dashboard and Enhancements (Add visibility and manual controls)


### Phase 1: 
- Create Spring AI project which will connect with the AI agents on the go, and provide PR review comments and summary on creating/updating the pull request.
- It will accept PR metadata from Github webhook and send the data to AI agent 
- The project connects with OpenAI to run `gemma` AI agent
  `summarize the following pull request diff and suggest improvements`

#### Endpoints:
- `/pr-analyze`
  - Return structured JSON output with summary, suggestions, and optional test case idea.
  - AI Prompt `As an expert programmer, review the following pull request difference and suggest improvements`
- `/generate-summary`
  - Return structured JSON output with the PR summary.
  - AI Prompt `Given the following PR title, description and optional difference, 
      generate a clear 1-2 sentence summary describing what this PR does and capture the summary of each file changed.`

### Phase 2:
- Integrate the response from the AI to Slack. 
- Create a Slack bot and push the message on Slack using Webhook (as its oneway communication)
- `AI Code Reviewer` posts review notes and summaries tto Slack automatically.

### Phase 3:
- Make AI context-aware using your actual codebase/docs. (Using RAG and Vector store)
- Index codebase using Langchain and ChromaDB
- Feed the context into your AI prompts for smarter suggestions.
- Create an endpoint `/ask-doc` to allow querying project-specific info.
- AI prompt `Given this PR and current service layer structure, is this new method placed correctly ?`
- In this phase we will be building 
  - A vector store (ChromaDB in this example) that indexes your code and documents 
    - Install ChromaDB https://docs.spring.io/spring-ai/reference/api/vectordbs/chroma.html#_run_chroma_locally
    - You will use OpenAI embeddings 
    - Add files under the folder `src/main/java` and readme. Include any other folder where the documents are present.
    - Splitting larger files into chunks, embed and store. 
  - A pipeline to retrieve relevant chunks when a PR comes in (from Phase 1).
    - Improve the AIService to query the vector database and include the context for the files which are changed in the PR.
  - Improved AI prompt that include retrieved context like 
    `Given the following context from the codebase and this PR diff, summarize and suggest improvements`

### Phase 4:
- Use multi agents for dedicated tasks
- Add agents using LangGraph or AutoGen
  - Code Reviewer Agent
  - Unit Test Generator Agent
  - Jira Scrum Assistant Agent
- Orchestrate conversation between them (multi-agent loop)

### Phase 5 (Optional):
- Create a React dashboard
- Add features like
  - Logs of each AI message
  - PR AI summaries
  - Ask-AI console for custom prompts

#### Enhancements
- [ ] Security
- [x] Logging
- [ ] Tracing
- [x] Extract PR summary for each file
- [ ] Project Re-structure into different modules
- [ ] Add PR URL and Author for Slack communication.

### Blogs series
- Meet your smart devops assistant: Project Vision and Stack
- Github meets AI: Auto-reviewing PR with LLMs
- Training the Brain: How I used RAG for Smarter PR Analysis
- AI Agents in Action: Building multi-agent pipelines for Devops