# Spring AI Intro: Capital Information Service

## Overview
Spring AI Intro is a modern Spring Boot application that leverages OpenAI's capabilities to provide information about capital cities. The service offers simple, efficient endpoints for querying capital cities and receiving AI-powered responses.

## Features
- Get capital city names for any country or state
- Receive structured JSON responses
- Simple and clean API interface
- RESTful endpoints for easy integration

## Tech Stack
- Spring Boot
- Spring AI
- OpenAI Integration
- Java 21
- Maven

## Prerequisites
- JDK 21
- Maven
- OpenAI API key
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

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
