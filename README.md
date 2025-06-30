# Spring AI Intro: From Experiment to Features

## Overview
A small reference project that demonstrates how to:

* build a **Spring Boot 3.5** application on **Java 21**
* call OpenAI models through *spring-ai*
* store and retrieve embeddings in **Milvus**
* read and chunk documents with the *Tika* document reader
* expose simple REST end-points
* **NEW:** enrich answers with data fetched from the public  
  [API Ninjas](https://api-ninjas.com/api) catalogue

## Features
- Get capital city names for any country or state
- Receive structured JSON responses
- Simple and clean API interface
- RESTful endpoints for easy integration
- RAG with Vector Database Milvus
- AI Tools for Weather and Stock Price

## Prompt Engineering

- Prompt Engineering Whitepaper from Google: [link](https://www.kaggle.com/whitepaper-prompt-engineering)
- Anthropic's Prompt Engineering Guide: [link](https://docs.anthropic.com/en/docs/build-with-claude/prompt-engineering/overview)
- OpenAI's 3-Part Video Series About Prompt Engineering:
    - Introduction to Prompt Engineering: [link](https://academy.openai.com/public/videos/introduction-to-prompt-engineering-2025-02-13)
    - Advanced Prompt Engineering [link](https://academy.openai.com/public/videos/advanced-prompt-engineering-2025-02-13)
    - Mastering Prompt Engineering: [link](https://academy.openai.com/public/videos/mastering-prompts-the-key-to-getting-what-you-need-from-chatgptmastering-prompts-the-key-to-getting-what-you-need-from-chatgpt-2025-03-20)
- OpenAI's Reasoning Models:
    - Best Practices for Working with Reasoning Models: [link](https://platform.openai.com/docs/guides/reasoning-best-practices)

## Tech Stack
- Spring Boot
- Spring AI
- OpenAI Integration
- Java 21
- Docker
- Maven

## Prerequisites
- JDK 21
- Maven
- OpenAI API key
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 2  Configuration

All secrets are read from environment variables or the `.env` file in the
project root (use the provided `.env.template` as a starting point):

| Variable | Purpose |
|----------|---------|
| `OPENAI_API_KEY`       | key for OpenAI completions / chat |
| `API_NINJAS_KEY`       | key for all Api-Ninjas calls |
| `MILVUS_IMAGE`         | Milvus Docker image tag (defaults to latest) |

Example:
```
bash
# .env
OPENAI_API_KEY=sk-********************************
API_NINJAS_KEY=ninjas_********************************
```

## Quick Start

### Installation

1. Clone the repository:
```shell script
git clone [repository-url]
cd spring-ai-intro
```

2. Configure OpenAI:
   Create `application.properties` in `src/main/resources/` and add:
```properties
spring.ai.openai.api-key=your-api-key-here
```

3. Build and run:
```shell script
mvn clean install
mvn spring-boot:run
```

## API Usage

### Get Capital City
**Endpoint:** `POST /api/capital`

**Request:**
```json
{
    "stateOrCountry": "France"
}
```

**Response:**
```json
{
    "answer": "Paris"
}
```

## Project Structure
```
spring-ai-intro/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       └── templates/
│   └── test/
└── pom.xml
```

## Development

### Building
```shell script
mvn clean install
```

### Running Tests
```shell script
mvn test
```

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support
For support and questions, please open an issue in the repository.
