Designed and developed a production-ready AI Customer Support Agent leveraging Spring AI 2.0, Google Gemini, and PostgreSQL PgVector to deliver real-time intelligent conversations, autonomous function execution, and grounded knowledge retrieval.
Key Highlights & Architecture:
• Autonomous Tool Calling: Integrated Spring AI function calling allowing the agent to dynamically inspect order statuses and trigger order cancellations based on user intent.
• Enterprise RAG Pipeline: Built an end-to-end document ingestion ETL pipeline using Apache Tika to parse multi-format documents (PDF, DOCX, TXT), chunked with TokenTextSplitter, and embedded via Gemini Embeddings (768 dimensions).
• Persistent Vector Search: Configured PostgreSQL with the pgvector extension using HNSW indexing and cosine similarity for sub-second semantic retrieval, integrated seamlessly via Spring AI QuestionAnswerAdvisor.
• Interactive Web Interface: Created a responsive chat and knowledge base management UI with Thymeleaf, Vanilla CSS, and JavaScript supporting drag-and-drop document uploads and real-time query streaming.
• Containerized Deployment: Orchestrated the entire persistence and database inspection layer (pgvector and pgAdmin) via Docker Compose.

Tech Stack: Java 21, Spring Boot 4, Spring AI 2.0, Google GenAI (Gemini), PgVector, PostgreSQL, Apache Tika, Docker, Thymeleaf, REST APIs.
