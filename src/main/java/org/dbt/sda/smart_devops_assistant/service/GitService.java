package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.entities.GitChangedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

}
