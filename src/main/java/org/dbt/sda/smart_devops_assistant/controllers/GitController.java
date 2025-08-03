package org.dbt.sda.smart_devops_assistant.controllers;

import org.dbt.sda.smart_devops_assistant.entities.GitChangedFile;
import org.dbt.sda.smart_devops_assistant.service.GitService;
import org.dbt.sda.smart_devops_assistant.service.GitWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/git")
public class GitController {

    private static final Logger logger = LoggerFactory.getLogger(GitController.class);

    GitService gitService;

    public GitController(GitService gitService) {
        this.gitService = gitService;
    }

    @GetMapping("/status")
    public String allOK(){
        return "Git Side All OK!";
    }

    @GetMapping("/pr/{prNumber}")
    public ResponseEntity<List<GitChangedFile>> analyzeFilesInPR(@PathVariable String prNumber){
        ResponseEntity<List<GitChangedFile>> responseEntity = gitService.fetchPRFiles(prNumber);
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            List<GitChangedFile> filesChanged = responseEntity.getBody();
            assert filesChanged != null;


            return responseEntity;
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/file/content/{sha}")
    public String fetchFileContentFromGit(@PathVariable String sha){
        String  encodedContent = gitService.fetchChangedFileContentFromGit(gitService.generateGitURL(sha));
        encodedContent = encodedContent.replaceAll("\\n", "");
        logger.debug("encoded file content {}", encodedContent);
        String decodedStr = new String(Base64.getDecoder().decode(encodedContent));
        logger.debug("decodeStr {}", decodedStr);
        return decodedStr;
    }
}
