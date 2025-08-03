package org.dbt.sda.smart_devops_assistant.config;

import org.dbt.sda.smart_devops_assistant.dto.GitFileMetaDataDTO;
import org.dbt.sda.smart_devops_assistant.entities.GitFileMetaData;
import org.dbt.sda.smart_devops_assistant.mapper.GitFileMetaDataMapper;
import org.dbt.sda.smart_devops_assistant.repo.GitFileMetaDataRepository;
import org.dbt.sda.smart_devops_assistant.service.FileService;
import org.dbt.sda.smart_devops_assistant.service.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {


    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    FileService fileService;
    GitService gitService;
    GitFileMetaDataRepository gitFileMetaDataRepository;
    GitFileMetaDataMapper gitFileMetaDataMapper;

    @Value("${git.branch.name:master}")
    String GIT_BRANCH_NAME;

    public DataInitializer(FileService fileService,
                           GitService gitService,
                           GitFileMetaDataRepository gitFileMetaDataRepository,
                           GitFileMetaDataMapper gitFileMetaDataMapper) {
        this.fileService = fileService;
        this.gitService = gitService;
        this.gitFileMetaDataRepository = gitFileMetaDataRepository;
        this.gitFileMetaDataMapper = gitFileMetaDataMapper;
    }

    void loadData() {
        // 1. Run the API https://api.github.com/repos/{{owner}}/{{repo}}/branches/{{branch_name}}
        // 2. Fetch the SHA
        // 3. Run the API https://api.github.com/repos/{{owner}}/{{repo}}/git/trees/{{sha}}?recursive=1
        // 4. Convert the Response in GitFileMetaData
        // 5. Save the list of GitFileMetaData to database (H2).
        log.info("Initial VectorStore data load started...");
        String sha = gitService.fetchBranchSHA(GIT_BRANCH_NAME);
        List<GitFileMetaData> gitFileMetaDataList = gitService.fetchGitFileMetaData(sha);
        gitFileMetaDataRepository.saveAll(gitFileMetaDataMapper.toGitFileMetaDataDTOList(gitFileMetaDataList));
        log.info("Initial VectorStore data load completed!");

        log.info("Listing the entities saved in H2 DB");
        List<GitFileMetaDataDTO> list = gitFileMetaDataRepository.findAll();
        list.forEach(dto -> log.info(dto.toString()));
        log.info("-------------------------------------");
    }

    @Override
    public void run(String... args) {
        loadData();
    }
}
