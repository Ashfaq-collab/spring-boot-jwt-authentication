# Spring Boot JWT Authentication

A REST API project built using Java and Spring Boot to implement JWT (JSON Web Token) based authentication and secure protected endpoints using Spring Security.

## About the Project

This project demonstrates how JWT authentication works in a Spring Boot application. Users can register, log in with their credentials, receive a JWT, and access protected REST APIs by sending the token in the Authorization header.

This project was developed for learning and practising Spring Security and JWT authentication.

## Technologies Used

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- H2 In-Memory Database
- Maven
- JSON Web Token (JWT) using JJWT
- Postman

## Features

- User registration
- Password hashing using BCrypt
- User login and credential verification
- JWT token generation
- JWT signature and expiration validation
- Stateless authentication
- Protected REST API endpoints
- User persistence using H2 Database

## API Endpoints

| Method | Endpoint         | Description                             | Access       |
| ------ | ---------------- | --------------------------------------- | ------------ |
| POST   | `/auth/register` | Register a new user                     | Public       |
| POST   | `/auth/login`    | Authenticate and generate a JWT         | Public       |
| GET    | `/user/profile`  | Access the authenticated user's profile | JWT required |

## Authentication Flow

1. The user registers with a username and password.
2. The application hashes the password using BCrypt and stores the user in the database.
3. The user logs in with their credentials.
4. Spring Security validates the credentials.
5. The application generates a signed JWT.
6. The client sends the JWT in the `Authorization` header when accessing protected APIs.
7. The JWT authentication filter validates the token and establishes authentication.
8. Spring Security allows access to the protected endpoint if authentication succeeds.

## Sample API Requests

### 1. Register a User

**POST** `/auth/register`

Request body:

```json
{
  "username": "rahul",
  "password": "yourPassword123"
}
```

### 2. Login

**POST** `/auth/login`

Request body:

```json
{
  "username": "rahul",
  "password": "yourPassword123"
}
```

Example response:

```json
{
  "token": "<your-jwt-token>",
  "tokenType": "Bearer"
}
```

### 3. Access a Protected Endpoint

**GET** `/user/profile`

Request header:

```http
Authorization: Bearer <your-jwt-token>
```

Example response:

```json
{
  "message": "You accessed a protected endpoint!",
  "username": "rahul"
}
```

## Prerequisites

- Java 17 or later, depending on the project's configured Java version
- Maven, or the Maven Wrapper included in the project
- An IDE such as IntelliJ IDEA, Eclipse, or VS Code
- Postman for testing REST APIs

## How to Run the Project

1. Clone the repository:

   ```bash
   git clone <your-repository-url>
   ```

2. Open the project in your IDE.

3. Check the database and JWT configuration in `src/main/resources/application.properties`.

4. Run the Spring Boot application using your IDE or the Maven Wrapper:

   **Windows:**

   ```bash
   mvnw.cmd spring-boot:run
   ```

   **Linux/macOS:**

   ```bash
   ./mvnw spring-boot:run
   ```

5. The application will typically run at:

   `http://localhost:8080`

6. Use Postman to test the registration, login, and protected endpoints.

## Database

This project uses the H2 in-memory database for storing user details.

Database records are temporary and are normally lost when the application stops.

If the H2 console is enabled and configured, it can be accessed at:

`http://localhost:8080/h2-console`

## Project Structure

```text
src/main/java/com/example/jwtauth/
├── controller/
│   ├── AuthController.java
│   └── UserController.java
├── dto/
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── entity/
│   └── User.java
├── repository/
│   └── UserRepository.java
├── security/
│   ├── SecurityConfig.java
│   ├── JwtService.java
│   └── JwtAuthenticationFilter.java
└── service/
    └── UserService.java
```

*Note: Adjust the package name and file list to match your actual project structure.*

## Security Notes

- Passwords are stored as BCrypt hashes rather than plain text.
- JWTs are signed and have an expiration time.
- Never commit real secrets, passwords, or access tokens to GitHub.
- The H2 in-memory database is intended for development and learning.
- Additional security hardening is needed before production deployment.

## What I Learned

- Building REST APIs using Spring Boot
- Implementing authentication with Spring Security
- Understanding `UserDetailsService` and `AuthenticationManager`
- Hashing passwords using BCrypt
- Generating and validating JWTs
- Implementing a custom JWT authentication filter
- Protecting REST endpoints
- Testing APIs using Postman

## Author

**Mohammed Ashfaq M**

---

This project was created as a hands-on learning exercise to understand JWT authentication with Spring Boot and Spring Security.
