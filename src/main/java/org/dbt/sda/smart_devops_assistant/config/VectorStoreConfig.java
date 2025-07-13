package org.dbt.sda.smart_devops_assistant.config;

import org.dbt.sda.smart_devops_assistant.service.FileService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class VectorStoreConfig {

    VectorStore vectorStore;
    FileService fileService;

    public VectorStoreConfig(VectorStore vectorStore, FileService fileService) {
        this.vectorStore = vectorStore;
        this.fileService = fileService;
    }

    void loadSrcFiles() {
        List<Document> documents = new ArrayList<>();
        try{
            List<File> files = fileService.readFromDir("src/main/java");
            files.forEach(file -> {
                                try {
                                    Document document = Document.builder()
                                            .id(file.getAbsolutePath())
                                            .metadata("fileName", file.getName())
                                            .text(Arrays.toString(Files.readAllBytes(file.toPath())))
                                            .build();
                                    documents.add(document);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
            vectorStore.add(documents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
