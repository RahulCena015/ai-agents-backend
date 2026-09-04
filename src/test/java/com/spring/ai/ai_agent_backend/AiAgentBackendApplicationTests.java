package com.spring.ai.ai_agent_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiAgentBackendApplicationTests {

	@org.springframework.beans.factory.annotation.Autowired
	private com.spring.ai.ai_agent_backend.service.DocumentService documentService;

	@org.springframework.beans.factory.annotation.Autowired
	private org.springframework.ai.vectorstore.VectorStore vectorStore;

	@Test
	void contextLoads() {
	}

	@Test
	void testIngestDocument() {
		String content = "Hello world! This is a test document.";
		org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(content.getBytes()) {
			@Override
			public String getFilename() {
				return "data.txt";
			}
		};
		documentService.ingestDocument(resource, "data.txt");
	}

	@Test
	void testQuerySimilarity() {
		var results = vectorStore.similaritySearch(
				org.springframework.ai.vectorstore.SearchRequest.builder()
						.query("In which company Rahul works?")
						.topK(5)
						.similarityThresholdAll()
						.build()
		);
		for (var doc : results) {
			System.out.println("DOC ID: " + doc.getId() + " | SCORE: " + doc.getScore() + " | CONTENT: " + doc.getText());
		}
	}
}
