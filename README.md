# WireMock API Proxy

## Overview

This project is a simple API proxy that leverages **WireMock** to simulate responses from an external API for testing
purposes. It is built using **Spring Boot** and provides an endpoint to fetch currency exchange rates from a mocked API.

## Features

- Mocking external API responses using **WireMock**
- REST API built with **Spring Boot**
- Automated testing with **JUnit 5** and **RestAssured**
- Dynamic properties configuration

## Technologies Used

- **Java 17**
- **Spring Boot 3.2.3**
- **WireMock** for API stubbing
- **RestAssured** for API testing
- **JUnit 5** for unit and integration tests
- **Maven** for dependency management

## Getting Started

### Prerequisites

Ensure you have the following installed:

- Java 17
- Maven 3.x

### Installation

1. Clone the repository:

   ```sh
   git clone https://github.com/your-repo/wiremock-api-proxy.git
   cd wiremock-api-proxy
   ```

2. Build the project:

   ```sh
   mvn clean install
   ```

## API Endpoints

### Get Exchange Rate

Retrieves the exchange rate for a given currency.

#### Request:

```
GET /currency?code=USD
```

#### Response (200 OK):

```json
{
  "table": "A",
  "currency": "dolar amerykański",
  "code": "USD",
  "rates": [
    {
      "no": "025/A/NBP/2024",
      "effectiveDate": "2024-02-05",
      "mid": 4.0321
    }
  ]
}

```

#### Response (404 Not Found):

```json
{
  "error": "Currency not found"
}
```

## Running Tests

The project includes tests for API endpoints using **WireMock** and **RestAssured**. To run tests:

```sh
mvn test
```

