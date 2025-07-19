package org.dbt.sda.smart_devops_assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dbt.sda.smart_devops_assistant.entities.GitChangedFile;
import org.dbt.sda.smart_devops_assistant.entities.GitFileMetaData;
import org.dbt.sda.smart_devops_assistant.entities.GitTreeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class GitService {

    private static final Logger logger = LoggerFactory.getLogger(GitService.class);
    @Value("${git.repo.name}")
    private String GIT_REPO_NAME;
    @Value("${git.owner}")
    private String GIT_OWNER;
    @Value("${git.api.url}")
    private String GITHUB_API_URL;
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestTemplate restTemplate;
    private Environment environment;


    public GitService(RestTemplateBuilder restTemplateBuilder, Environment environment) {
        this.restTemplate = restTemplateBuilder.build();
        this.environment = environment;
    }

    private HttpEntity generateHttpEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+ environment.getProperty("GIT_SDA_PAT"));
        headers.set("ContentType", MediaType.APPLICATION_JSON_VALUE);

        return new HttpEntity(headers);
    }

    public ResponseEntity<String> fetchPRDiff(String diffURL){
        if(Objects.nonNull(diffURL)){
            HttpEntity httpEntity = generateHttpEntity();
            return restTemplate.exchange(diffURL, HttpMethod.GET, httpEntity, String.class);
        }
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<GitChangedFile>> fetchPRFiles(String prNumber){
        try {
            if (Objects.nonNull(prNumber)) {
                HttpEntity httpEntity = generateHttpEntity();
                //https://api.github.com/repos/ankitagrahari/smart-devops-assistant/pulls/3/files
                String gitURL = GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/pulls/" + prNumber + "/files";
                logger.debug("Fetch PR files: gitURL:{}", gitURL);
                return restTemplate.exchange(gitURL, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {
                });
            }
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }

    public String fetchBranchSHA(String branchName) {
        try {
            HttpEntity httpEntity = generateHttpEntity();
//          https://api.github.com/repos/{{owner}}/{{repo}}/branches/{{branch_name}}
            String gitURL = GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/branches/" + branchName;
            logger.debug("Fetch files from branch {} gitURL:{}", branchName, gitURL);
            ResponseEntity<String> response = restTemplate.exchange(gitURL, HttpMethod.GET, httpEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode jsonNode = OBJECT_MAPPER.readTree(response.getBody());
                logger.info("Branches Response:{}", response.getBody());
                return jsonNode.get("commit").get("sha").asText();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON response", e);
        }
        return null;
    }

    public List<GitFileMetaData> fetchGitFileMetaData(String sha) {

        HttpEntity httpEntity = generateHttpEntity();
//          https://api.github.com/repos/{{owner}}/{{repo}}/git/trees/{{sha}}?recursive=1
        String gitURL = GITHUB_API_URL + "repos/" + GIT_OWNER + "/" + GIT_REPO_NAME + "/git/trees/" + sha + "?recursive=1";
        logger.debug("Fetch git tree gitURL:{}", gitURL);
        ResponseEntity<GitTreeResponse> response = restTemplate.exchange(gitURL, HttpMethod.GET, httpEntity, GitTreeResponse.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            assert response.getBody() != null;
            return response.getBody().tree();
        }

        return Collections.EMPTY_LIST;
    }
}
