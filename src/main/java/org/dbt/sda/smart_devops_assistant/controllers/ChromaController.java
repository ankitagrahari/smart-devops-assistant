package org.dbt.sda.smart_devops_assistant.controllers;

import org.dbt.sda.smart_devops_assistant.entities.ChromaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chroma")
public class ChromaController {

    private static final Logger log = LoggerFactory.getLogger(ChromaController.class);
    ChromaService chromaService;

    public ChromaController(ChromaService chromaService) {
        this.chromaService = chromaService;
    }

    @GetMapping
    public String allOk(){
        return "Chroma API working...";
    }

    @GetMapping("/data")
    public List<Document> fetchDataByQuery(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "expression", required = false) String expression){
        log.info("Query {}, expression: {}", query, expression);

        return chromaService.fetchDocumentsByQuery(query, expression);
    }

    @DeleteMapping("/deleteCollection")
    public void deleteCollection(){
        chromaService.deleteCollection();
    }
}
