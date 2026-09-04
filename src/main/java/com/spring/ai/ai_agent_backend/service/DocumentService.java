package com.spring.ai.ai_agent_backend.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class DocumentService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter textSplitter;
    private final List<String> ingestedDocuments = new CopyOnWriteArrayList<>();

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.textSplitter = new TokenTextSplitter();
    }

    /**
     * Ingest a document: read → split into chunks → embed → store in PgVector.
     */
    public String ingestDocument(Resource fileResource, String fileName) {
        try {
            // 1. Extract: Read the document using Tika (supports PDF, DOCX, TXT, etc.)
            TikaDocumentReader reader = new TikaDocumentReader(fileResource);
            List<Document> documents = reader.get();

            // 2. Add source metadata to each document
            for (Document doc : documents) {
                doc.getMetadata().put("source", fileName);
            }

            // 3. Transform: Split into smaller chunks for better retrieval
            List<Document> chunks = textSplitter.apply(documents);

            // 4. Load: Store chunks in PgVector (embeddings are generated automatically)
            vectorStore.add(chunks);

            // Track the document name
            if (!ingestedDocuments.contains(fileName)) {
                ingestedDocuments.add(fileName);
            }

            return "Successfully ingested '" + fileName + "' — " + chunks.size() + " chunks created.";
        } catch (Exception e) {
            throw new RuntimeException("Failed to ingest document: " + fileName, e);
        }
    }

    /**
     * Get the list of all ingested document names.
     */
    public List<String> getIngestedDocuments() {
        return Collections.unmodifiableList(ingestedDocuments);
    }

    /**
     * Remove a document from tracking (note: vector store entries persist).
     */
    public boolean removeDocument(String fileName) {
        return ingestedDocuments.remove(fileName);
    }
}
