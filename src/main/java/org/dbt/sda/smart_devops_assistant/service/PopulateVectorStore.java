package org.dbt.sda.smart_devops_assistant.service;

import org.dbt.sda.smart_devops_assistant.dto.GitFileMetaDataDTO;
import org.dbt.sda.smart_devops_assistant.entities.GitChangedFile;
import org.dbt.sda.smart_devops_assistant.repo.GitFileMetaDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PopulateVectorStore {

    private static final Logger logger = LoggerFactory.getLogger(PopulateVectorStore.class);
    private final String GIT_FILE_TYPE_BLOB = "blob";
    private static final String GIT_FILE_STATUS_REMOVED = "REMOVED";
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
        logger.info("populating git files content to vector store!");
        changedFiles.stream()
                .filter(PopulateVectorStore::filterOnRemovedStatus)
                .filter(PopulateVectorStore::filterOnFileType)
                .map(this::getGitFileMetaDataDTO)
                .map(this::fetchChangedGitFileContent)
                .forEach(this::addToVectorStore);

        logger.info("Incoming files from git PR {} files", changedFiles.size());
    }

    private static String decodeContent(String encodedStr) {
        return new String(Base64.getDecoder().decode(encodedStr.replaceAll("\\n", "")));
    }

    private Pair<String, String> fetchChangedGitFileContent(GitFileMetaDataDTO dto) {
        String encodedContent = gitService.fetchChangedFileContentFromGit(dto.getUrl());
        return Pair.of(dto.getPath(), decodeContent(encodedContent));
    }

    private static boolean filterOnFileType(GitChangedFile obj) {
        return obj.filename().endsWith(".java");
    }

    private static boolean filterOnRemovedStatus(GitChangedFile gitChangedFile) {
        return !gitChangedFile.status().equals(GIT_FILE_STATUS_REMOVED);
    }

    private GitFileMetaDataDTO getGitFileMetaDataDTO(GitChangedFile gcf) {
        GitFileMetaDataDTO dto = gitRepo.findByPathAndType(gcf.filename(), GIT_FILE_TYPE_BLOB);
        if(Objects.nonNull(dto)) {
            logger.info("from vector store fetch:{}", dto);
        } else {
            String gitFileContentURL = gitService.generateGitURL(gcf.sha());
            logger.info("Not found in the H2 database. Fetch from URL {}", gitFileContentURL);
            dto = new GitFileMetaDataDTO(gcf.filename(), GIT_FILE_TYPE_BLOB, gcf.sha(), 0, gitFileContentURL);
            gitRepo.save(dto);
            logger.info("Entity saved to H2 for future use!");
        }
        return dto;
    }

    public void addToVectorStore(Pair<String, String> contentFilePair){

        String path = contentFilePair.getFirst();
        String content = contentFilePair.getSecond();

        Path tempFilePath = null;
        try {
            tempFilePath = getTempFilePath(content);

            Resource resource = new FileSystemResource(tempFilePath);
            DocumentReader reader = new TikaDocumentReader(resource);
            TextSplitter splitter = new TokenTextSplitter();
            List<Document> documents = splitter.apply(reader.get());

            documents.forEach(document -> {
                Map<String, Object> metaData = document.getMetadata();
                metaData.put("path", path);
            });

            vectorStore.add(documents);
        } catch (IOException e) {
            logger.error("Error creating temporary file {}", e.getMessage());
        } finally {
            if(tempFilePath!=null){
                try {
                    Files.deleteIfExists(tempFilePath);
                } catch (IOException e) {
                    logger.error("Error deleting the temp file {}", e.getMessage());
                }
            }
        }
    }

    private static Path getTempFilePath(String content) throws IOException {
        Path filePath = Files.createTempFile(Paths.get("src/main/resources"), "temp", "java");
        return Files.writeString(filePath, content);
    }
}
