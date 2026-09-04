package com.spring.ai.ai_agent_backend.controller;

import com.spring.ai.ai_agent_backend.service.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Upload a document (PDF, TXT, DOCX) to be ingested into the vector store.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadDocument(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File is empty"));
        }

        try {
            String fileName = file.getOriginalFilename();
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };

            String result = documentService.ingestDocument(resource, fileName);

            return ResponseEntity.ok(Map.of(
                    "message", result,
                    "fileName", fileName != null ? fileName : "unknown"
            ));
        } catch (Exception e) {
            String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to process file: " + cause));
        }
    }

    /**
     * List all ingested documents.
     */
    @GetMapping
    public ResponseEntity<List<String>> listDocuments() {
        return ResponseEntity.ok(documentService.getIngestedDocuments());
    }

    /**
     * Remove a document from the ingested list.
     */
    @DeleteMapping("/{fileName}")
    public ResponseEntity<Map<String, String>> deleteDocument(@PathVariable String fileName) {
        boolean removed = documentService.removeDocument(fileName);
        if (removed) {
            return ResponseEntity.ok(Map.of("message", "Document '" + fileName + "' removed."));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
