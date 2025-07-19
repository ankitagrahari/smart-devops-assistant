package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.dto.GitFileMetaDataDTO;
import org.dbt.sda.smart_devops_assistant.entities.GitChangedFile;
import org.dbt.sda.smart_devops_assistant.repo.GitFileMetaDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PopulateVectorStore {

    private static final Logger logger = LoggerFactory.getLogger(PopulateVectorStore.class);
    private final String GIT_FILE_TYPE_BLOB = "blob";
    GitFileMetaDataRepository gitRepo;
    GitService gitService;
    VectorStore vectorStore;

    public PopulateVectorStore(GitFileMetaDataRepository gitRepo,
                               GitService gitService,
                               VectorStore vectorStore) {
        this.gitRepo = gitRepo;
        this.gitService = gitService;
        this.vectorStore = vectorStore;
    }

    public void populateVectorStore(List<GitChangedFile> changedFiles){
        changedFiles.stream()
                .map(GitChangedFile::filename)
                .map(fileName -> gitRepo.findByPathAndType(fileName, GIT_FILE_TYPE_BLOB))
                .map(GitFileMetaDataDTO::getUrl)
                .map(url -> gitService.fetchChangedFileContentFromGit(url))
                .map(content -> content.replaceAll("\\n", System.lineSeparator()))
                .map(encodedStr -> new String(Base64.getDecoder().decode(encodedStr)))
                .forEach(this::addToVectorStore);
    }

    private void addToVectorStore(String content){
        Resource codeResource = new ByteArrayResource(content.getBytes());
        DocumentReader reader = new TikaDocumentReader(codeResource);
        TextSplitter splitter = new TokenTextSplitter();
        vectorStore.add(splitter.apply(reader.get()));
    }
}
