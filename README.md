
# Hare Business Consulting Backend Coding Test

A RESTful Form Builder and Submission API built with Java and Spring Boot. This application allows users to create customizable forms, define dynamic question types (Multiple Choice, Checkboxes, Short Answer, Date, Time), and safely collect user responses with strict domain and access validations.

## Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 4.0.6
* **Security:** Spring Security & JWT (JSON Web Tokens)
* **Database:** PostgreSQL
* **Migrations:** Flyway
* **Testing:** JUnit 5 & Mockito

## Prerequisites
[Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.

## Quick Start
1. Clone the repository
2. Launch the environment:
```
docker-compose up --build
```
3. The application will be available at http://localhost:8080. The API documentation will be available at http://localhost:8080/swagger-ui/index.html