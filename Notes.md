# Spring Boot JWT Authentication — Complete Project Notes

## 1. Project Overview

**Project name:** JWT Authentication using Spring Boot

**Objective:** Build a REST API that allows users to register, log in, receive a JWT, and access protected endpoints using that token.

### Technologies used

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- H2 Database
- Maven
- JJWT library
- Postman

### API endpoints

| HTTP method | Endpoint | Purpose | Access |
|---|---|---|---|
| POST | `/auth/register` | Register a new user | Public |
| POST | `/auth/login` | Authenticate and generate a JWT | Public |
| GET | `/user/profile` | Return the authenticated user's profile | Protected |

## 2. What Is JWT?

JWT stands for JSON Web Token.

It is a compact token format that allows a server to verify claims about a user or other subject.

After a user successfully logs in, the server generates a signed token. The client sends that token with subsequent requests to protected APIs.

The server verifies the token and establishes the user's authentication for the request.

### JWT structure

A JWT commonly consists of three parts:

`header.payload.signature`

**Header:** Contains token metadata, such as the signing algorithm.

**Payload:** Contains claims such as the username, issue time, and expiration time.

**Signature:** Allows the server to verify that the signed token has not been altered.

Our token contains these claims:

- `sub`: the username, such as `rahul`.
- `iat`: the time the token was issued.
- `exp`: the time the token expires.

JWT payloads are encoded, not encrypted. Do not store passwords or confidential information in the payload.

## 3. What Is the Difference Between Authentication and Authorization?

**Authentication** answers: Who are you?

Example: The server verifies Rahul's username and password during login.

**Authorization** answers: What are you allowed to access?

Example: The server permits an authenticated user to access `/user/profile`, subject to the configured access rules.

Spring Security supports both.

## 4. Project Package Structure

Our project follows a layered structure.

```text
com.example.jwtauth
|
|-- JwtAuthApplication.java
|
|-- entity
|   |-- User.java
|
|-- repository
|   |-- UserRepository.java
|
|-- dto
|   |-- RegisterRequest.java
|   |-- LoginRequest.java
|   |-- LoginResponse.java
|
|-- service
|   |-- UserService.java
|
|-- controller
|   |-- AuthController.java
|   |-- UserController.java
|
|-- security
    |-- SecurityConfig.java
    |-- JwtService.java
    |-- JwtAuthenticationFilter.java
```

Each package has a different responsibility.

- **entity:** Represents persistent database data.
- **repository:** Performs database operations.
- **dto:** Defines the data exchanged through APIs.
- **service:** Contains business logic.
- **controller:** Handles HTTP requests and responses.
- **security:** Contains authentication configuration and JWT functionality.

This separation makes the application easier to understand, test, and maintain.

## 5. Important Classes and Interfaces

### A. `JwtAuthApplication`

This is the main Spring Boot application class.

It contains the `main()` method and the `@SpringBootApplication` annotation.

**Why is it needed?**

It starts the application and triggers Spring Boot's application context initialization.

`@SpringBootApplication` combines three important annotations:

- `@Configuration`: Allows the class to declare Spring beans.
- `@EnableAutoConfiguration`: Lets Spring Boot configure components based on the available dependencies and configuration.
- `@ComponentScan`: Finds eligible Spring components in the application's package and its subpackages.

### B. `User` — Entity class

**Package:** `entity`

Represents a user record in the database.

Important annotations:

- `@Entity`: Marks the class as a JPA entity.
- `@Table(name = "users")`: Specifies the database table name.
- `@Id`: Identifies the primary key.
- `@GeneratedValue`: Configures ID generation.
- `@Column`: Configures database column constraints.

Fields:

- `id`: Unique identifier.
- `username`: Unique username.
- `password`: Encoded password hash.

JPA requires an accessible no-argument constructor for entity instantiation.

**Why do we need it?**

The entity provides the mapping between a Java object and a database record.

### C. `UserRepository` — Repository interface

**Package:** `repository`

```java
public interface UserRepository
        extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

This interface extends Spring Data JPA's `JpaRepository`.

`JpaRepository<User, Long>` means:

- `User`: The entity managed by this repository.
- `Long`: The type of its primary key.

Spring Data JPA creates the implementation automatically.

Important methods:

- `save(user)`: Saves or updates a user.
- `findById(id)`: Finds a user by ID.
- `findAll()`: Retrieves all users.
- `findByUsername(username)`: Finds a user by username.
- `existsByUsername(username)`: Checks whether a username exists.

The last two methods are derived query methods. Spring Data JPA derives their database queries from their names.

**Why do we need it?**

It provides database access without requiring us to manually implement every CRUD operation.

### D. `RegisterRequest`, `LoginRequest`, and `LoginResponse` — DTO classes

DTO stands for Data Transfer Object.

These classes define the information exchanged between the client and server.

`RegisterRequest` receives registration details.

`LoginRequest` receives login credentials.

`LoginResponse` returns the generated token and token type.

Important annotations used in request DTOs:

- `@NotBlank`: Rejects null, empty, or whitespace-only strings.
- `@Size`: Enforces length constraints.
- `@Valid`: Triggers validation of the request object when used with Spring MVC.

**Why do we need DTOs?**

They separate API request and response formats from database entities. They also help us control which fields clients can submit or receive.

### E. `UserService` — Service class

**Package:** `service`

Contains the business logic for registration and login.

Important annotations:

- `@Service`: Registers the class as a Spring-managed service component.
- `@Transactional`: Runs the annotated operation within a database transaction.

During registration, the service:

1. Checks whether the username already exists.
2. Encodes the password using the password encoder.
3. Creates a `User` object.
4. Saves the user through `UserRepository`.

During login, the service:

1. Passes the submitted credentials to `AuthenticationManager`.
2. Allows Spring Security to verify the credentials.
3. Generates a JWT through `JwtService` after successful authentication.

**Why do we need it?**

It keeps business logic separate from HTTP request handling and database access.

### F. `AuthController` — REST controller

**Package:** `controller`

Important annotations:

- `@RestController`: Defines a controller whose return values are written to the HTTP response body.
- `@RequestMapping("/auth")`: Defines a common URL prefix.
- `@PostMapping("/register")`: Maps registration requests.
- `@PostMapping("/login")`: Maps login requests.
- `@RequestBody`: Converts incoming JSON into a Java object.
- `@Valid`: Triggers request validation.

The controller delegates work to `UserService` instead of performing database operations itself.

**Why do we need it?**

It provides the HTTP interface through which Postman or another client interacts with the application.

### G. `UserController` — Protected REST controller

Contains the endpoint:

`GET /user/profile`

Its method accepts Spring Security's `Authentication` object.

The controller uses `authentication.getName()` to retrieve the current authenticated principal's name.

**Why do we need it?**

It demonstrates that authentication established by the JWT filter can be used by a protected REST endpoint.

### H. `SecurityConfig` — Security configuration class

**Package:** `security`

Important annotations:

- `@Configuration`: Declares configuration and bean definitions.
- `@EnableWebSecurity`: Enables Spring Security's web security integration.
- `@Bean`: Registers an object with the Spring application context.

This class configures:

- `PasswordEncoder`
- `UserDetailsService`
- `DaoAuthenticationProvider`
- `AuthenticationManager`
- `SecurityFilterChain`

It also defines which endpoints are public and which require authentication.

**Why do we need it?**

Without the appropriate security configuration, Spring Security's defaults may prevent our custom registration and login flow from working as intended.

### I. `PasswordEncoder` — Interface

Package: `org.springframework.security.crypto.password`

This interface defines operations for encoding passwords and verifying a submitted password against an encoded password.

Our implementation is:

`BCryptPasswordEncoder`

Important methods:

- `encode(rawPassword)`: Produces a password hash.
- `matches(rawPassword, encodedPassword)`: Checks whether a raw password matches the stored hash.

**Why do we need it?**

Passwords must not be stored in plain text. BCrypt provides password hashing designed to make offline password guessing more expensive.

A password hash is not a JWT and does not need to be decrypted during login.

### J. `UserDetailsService` — Interface

Package: `org.springframework.security.core.userdetails`

Defines the method:

`loadUserByUsername(username)`

Our implementation uses `UserRepository` to retrieve a user from H2 and converts the database record into Spring Security's `UserDetails` representation.

**Why do we need it?**

Spring Security needs a standard way to load a user's credentials and authorities when authenticating login requests.

### K. `UserDetails` — Interface

Represents user information used by Spring Security.

It provides information such as:

- Username.
- Password hash.
- Granted authorities.
- Account status flags.

Our application uses Spring Security's built-in `User` implementation to construct a `UserDetails` object.

**Why do we need it?**

It gives Spring Security a standard representation of a user, independent of our database entity.

### L. `AuthenticationManager` — Interface

Package: `org.springframework.security.authentication`

The main method is:

`authenticate(Authentication authentication)`

It delegates the authentication attempt to an appropriate authentication provider.

In our project, the submitted username and password are represented by a `UsernamePasswordAuthenticationToken`.

**Why do we need it?**

It lets our login service use Spring Security's authentication machinery rather than manually comparing passwords.

If authentication fails, the manager throws an authentication exception and our login service does not generate a JWT.

### M. `DaoAuthenticationProvider` — Class

Package: `org.springframework.security.authentication.dao`

This authentication provider uses a `UserDetailsService` to load the user and a `PasswordEncoder` to verify the submitted password.

Our configuration connects it to:

- `UserDetailsService`
- `BCryptPasswordEncoder`

**Why do we need it?**

It implements the standard database-backed username/password authentication process used during login.

### N. `AuthenticationConfiguration` — Class

Package: `org.springframework.security.config.annotation.authentication.configuration`

Our configuration uses:

`configuration.getAuthenticationManager()`

to obtain Spring Security's configured authentication manager.

**Why do we need it?**

It allows us to expose the configured `AuthenticationManager` as a bean so our service can invoke authentication explicitly.

### O. `SecurityFilterChain` — Interface

Package: `org.springframework.security.web`

A `SecurityFilterChain` represents the security filters applied to matching HTTP requests.

Our bean configures rules such as:

- `/auth/**` is public.
- Other endpoints require authentication.
- HTTP Basic and form login are disabled in the final JWT configuration.
- The session policy is stateless.
- The JWT filter is placed in the security filter chain.

**Why do we need it?**

It defines how incoming requests are processed and protected by Spring Security.

### P. `JwtService` — Application service class

**Package:** `security`

This is our own class, not a Spring Boot framework class.

It uses the JJWT library to generate and parse JWTs.

Important methods:

- `generateToken(username)`: Creates and signs a JWT.
- `extractClaims(token)`: Parses the token and verifies its signature and validity.
- `extractUsername(token)`: Reads the subject claim.

The service uses a configured secret key and expiration duration.

**Why do we need it?**

It centralizes token creation and validation so we don't duplicate cryptographic logic in controllers or filters.

### Q. `JwtAuthenticationFilter` — Custom filter class

**Package:** `security`

Extends:

`OncePerRequestFilter`

This class is our own filter, built using Spring Framework and Spring Security APIs.

Its job is to:

1. Read the `Authorization` header.
2. Look for the `Bearer ` prefix.
3. Extract the token.
4. Verify the JWT through `JwtService`.
5. Load the user through `UserDetailsService`.
6. Create an authenticated `Authentication` object.
7. Store the authentication in `SecurityContextHolder`.
8. Continue the filter chain.

**Why do we need it?**

Generating a JWT at login is not enough. The application must also process that token on later requests. This filter connects JWT validation to Spring Security's authentication context.

### R. `OncePerRequestFilter` — Abstract class

Package: `org.springframework.web.filter`

Provides a base class for filters intended to execute once per request within a request dispatch.

It defines the filter lifecycle and provides the method:

`doFilterInternal(request, response, filterChain)`

Our JWT filter overrides that method.

**Why do we need it?**

It provides a convenient foundation for implementing custom HTTP request filtering without manually implementing the entire servlet filter contract.

### S. `SecurityContextHolder` — Class

Package: `org.springframework.security.core.context`

Provides access to the current security context.

Our JWT filter calls:

`SecurityContextHolder.getContext().setAuthentication(authentication)`

This establishes the authenticated identity for the current request's security processing.

**Why do we need it?**

Spring Security uses the context to determine which principal is authenticated when an endpoint is accessed.

In our stateless setup, the context is normally established again for each incoming request using its JWT.

### T. `Authentication` — Interface

Package: `org.springframework.security.core`

Represents an authentication request or the result of authentication.

After successful authentication, it provides information about the principal and their authorities.

The `Authentication` object received by `UserController` allows the endpoint to read the current user's name.

**Why do we need it?**

It provides a standard way to represent the identity and authentication state of the current request.

### U. `UsernamePasswordAuthenticationToken` — Class

Package: `org.springframework.security.authentication`

We use it in two ways.

**During login:** It carries the submitted username and password to `AuthenticationManager` for verification.

**After JWT validation:** The filter creates an authenticated instance containing the `UserDetails` and granted authorities.

**Why do we need it?**

It provides Spring Security with an authentication object for username/password credentials and the authenticated principal.

### V. `WebAuthenticationDetailsSource` — Class

Package: `org.springframework.security.web.authentication`

Our filter uses it to populate request-related details on the authentication object.

**Why do we need it?**

It can attach details such as the remote address and session ID where applicable. These details are supplementary; they do not validate the JWT or authenticate the user by themselves.

### W. `ResponseEntity` — Class

Package: `org.springframework.http`

Used by the registration controller to return an HTTP status and response body.

For example:

`ResponseEntity.status(HttpStatus.CREATED).body(...)`

**Why do we need it?**

It gives the controller explicit control over HTTP status codes, headers, and response bodies.

### X. `Optional<User>` — Java type

`Optional` is part of the Java standard library, not Spring Boot.

Our repository uses it to represent a query that may or may not find a user.

**Why do we need it?**

It makes the possibility of a missing user explicit and helps avoid accidentally dereferencing a null result.

## 6. Spring Boot Framework vs Our Own Classes

It is important to distinguish framework types from classes we created.

| Type | Origin |
|---|---|
| `@SpringBootApplication` | Spring Boot |
| `@RestController` | Spring Framework |
| `@Service` | Spring Framework |
| `@Entity` | Jakarta Persistence |
| `JpaRepository` | Spring Data JPA |
| `PasswordEncoder` | Spring Security |
| `AuthenticationManager` | Spring Security |
| `SecurityFilterChain` | Spring Security |
| `OncePerRequestFilter` | Spring Framework |
| `User` | Our application |
| `UserService` | Our application |
| `JwtService` | Our application |
| `JwtAuthenticationFilter` | Our application |
| `AuthController` | Our application |

Spring Boot builds on the Spring Framework and configures many components automatically. Spring Security, Spring Data JPA, and the JJWT library provide additional functionality.

## 7. Complete Execution Flow

### Flow A: User Registration

Request:

`POST /auth/register`

Body:

```json
{
  "username": "rahul",
  "password": "rahul12345"
}
```

Execution sequence:

1. Postman sends the HTTP request.
2. Spring Security evaluates the request's security rules.
3. The `/auth/**` rule permits the registration request.
4. `AuthController` receives the request.
5. `@RequestBody` maps JSON to `RegisterRequest`.
6. `@Valid` triggers request validation.
7. The controller calls `UserService.register()`.
8. The service checks for an existing username through `UserRepository`.
9. `PasswordEncoder` encodes the password using BCrypt.
10. The service creates a `User` entity.
11. `UserRepository.save()` persists the entity through JPA/Hibernate.
12. The database stores the record.
13. The controller returns HTTP `201 Created`.

The password stored in the database is a BCrypt hash, not the original password.

### Flow B: User Login and JWT Generation

Request:

`POST /auth/login`

Body:

```json
{
  "username": "rahul",
  "password": "rahul12345"
}
```

Execution sequence:

1. Postman sends the login request.
2. Spring Security permits `/auth/login`.
3. `AuthController` receives the request.
4. `LoginRequest` represents the submitted credentials.
5. The controller calls `UserService.login()`.
6. The service creates a `UsernamePasswordAuthenticationToken` containing the submitted credentials.
7. The service calls `AuthenticationManager.authenticate()`.
8. `AuthenticationManager` delegates to `DaoAuthenticationProvider`.
9. The provider calls `UserDetailsService.loadUserByUsername()`.
10. The user is retrieved through `UserRepository`.
11. The stored password hash is supplied to Spring Security.
12. `PasswordEncoder` verifies the submitted password against the hash.
13. If authentication succeeds, `UserService` calls `JwtService.generateToken()`.
14. `JwtService` creates the subject, issue time, and expiration claims.
15. The JJWT library signs the token using the configured secret key.
16. `LoginResponse` returns the token to Postman.

If credentials are invalid, authentication fails and no JWT is generated.

### Flow C: Accessing a Protected API

Request:

`GET /user/profile`

Header:

`Authorization: Bearer <your-jwt>`

Execution sequence:

1. Postman sends the request with the JWT.
2. Spring Security applies the configured filter chain.
3. `JwtAuthenticationFilter` reads the `Authorization` header.
4. The filter extracts the token after the `Bearer ` prefix.
5. `JwtService.extractClaims()` parses the token and verifies its signature and expiration.
6. The subject claim supplies the username.
7. `UserDetailsService` loads the user and their authorities.
8. The filter creates an authenticated `UsernamePasswordAuthenticationToken`.
9. The filter places the authentication in `SecurityContextHolder`.
10. The remaining security filters process the request.
11. Spring Security checks whether the endpoint requires authentication.
12. The request is permitted because the user is authenticated.
13. `UserController` receives the request.
14. The controller reads the principal's name from `Authentication`.
15. The endpoint returns the profile response.

Example response:

```json
{
  "message": "You accessed a protected endpoint!",
  "username": "rahul"
}
```

### Flow D: Invalid or Missing Token

**Missing token**

The JWT filter continues the chain without setting authentication. The security rules then reject access to the protected endpoint with HTTP `401 Unauthorized`.

**Invalid or expired token**

The JWT parser rejects the token. Our filter returns HTTP `401 Unauthorized` with an error message.

**Important:** The filter's exception handling should also account for failures while loading a user, not just JWT parsing errors. Production APIs should have consistent error handling that avoids exposing internal exception details.

## 8. What Is the Filter Chain?

A filter chain is a sequence of components that process an incoming HTTP request before it reaches a controller.

Conceptually, our security flow is:

```text
HTTP Request
     |
     v
Spring Security Filter Chain
     |
     v
JWT Authentication Filter
     |
     v
Other Security Filters
     |
     v
Authorization Checks
     |
     v
Controller
     |
     v
HTTP Response
```

The actual chain contains more filters and details than this simplified diagram.

Our custom filter is registered before `UsernamePasswordAuthenticationFilter`. This places JWT processing early enough for the authenticated identity to be available during subsequent authorization.

## 9. Important Configuration Concepts

### `PasswordEncoder`

Used for password hashing and verification.

Our implementation is `BCryptPasswordEncoder`.

### `UserDetailsService`

Loads user details from the database.

### `AuthenticationManager`

Coordinates authentication.

### `DaoAuthenticationProvider`

Verifies username/password credentials using the user details service and password encoder.

### `SecurityFilterChain`

Defines request authorization and filter configuration.

### `SessionCreationPolicy.STATELESS`

Tells Spring Security not to create or use an HTTP session to persist the security context between requests.

The client sends a JWT with each protected request.

### `permitAll()`

Allows a matching endpoint without requiring an authenticated principal.

### `authenticated()`

Requires an authenticated principal to access the endpoint.

### `addFilterBefore()`

Places our JWT filter before a specified filter in the Spring Security chain.

## 10. Common Errors and Troubleshooting

### `401 Unauthorized` during login

Possible causes:

- The user was lost when the in-memory H2 database reset.
- The username or password is incorrect.
- The authentication provider is not configured correctly.
- The stored password was not encoded using the expected encoder.

### `401 Unauthorized` for a protected API

Possible causes:

- The Authorization header is missing.
- The `Bearer ` prefix is missing or malformed.
- The token is invalid or expired.
- The signing secret differs between token generation and validation.
- The user can no longer be loaded.

### Application fails to start

Check:

- Missing Maven dependencies.
- Compilation errors.
- Missing Spring beans.
- Incorrect configuration property names.
- An invalid or insufficiently strong JWT signing key.

### H2 loses its records

The URL `jdbc:h2:mem:jwtdb` creates an in-memory database. Its contents normally disappear when the application stops.

### H2 console is blocked

Spring Security may protect the H2 console. Console access requires appropriate development-only security configuration, and the console must not be exposed carelessly in production.

### Duplicate JWT filter registration

Because `JwtAuthenticationFilter` is a Spring bean and a servlet filter, Spring Boot may also register it with the servlet container independently of the Spring Security chain.

For a clean configuration, disable the container registration with a `FilterRegistrationBean` whose `setEnabled(false)` method is called, while keeping the filter registered inside `SecurityFilterChain`.

## 11. Important Security Improvements for Production

Our project is a learning implementation, not a complete production authentication system.

Before deploying it, consider:

- Store signing secrets outside source control.
- Use a strong randomly generated key.
- Use HTTPS.
- Keep passwords hashed using an appropriate password encoder.
- Validate request fields and handle errors consistently.
- Consider rate limiting and protections against brute-force login attempts.
- Define appropriate roles and authorities.
- Configure CORS only for trusted origins when browser clients require it.
- Review CSRF settings according to the authentication and browser storage strategy.
- Consider refresh tokens, revocation strategies, and key rotation where needed.
- Avoid logging passwords, bearer tokens, or signing secrets.
- Add automated tests for registration, login, valid tokens, invalid tokens, expiration, and authorization.

JWTs are usually not immediately revocable unless the application implements an additional mechanism, such as a denylist or token-version strategy. A short expiration time limits the lifetime of a stolen access token but does not make theft harmless.

## 12. Interview Questions to Practise

1. What is JWT, and why is it used?
2. What are the three parts of a JWT?
3. What is the difference between authentication and authorization?
4. Why do we use BCrypt instead of storing plain-text passwords?
5. What is the purpose of `UserDetailsService`?
6. What is the difference between `AuthenticationManager` and `AuthenticationProvider`?
7. What does `SecurityFilterChain` do?
8. Why do we extend `OncePerRequestFilter`?
9. What is `SecurityContextHolder`?
10. Why do we use `JpaRepository`?
11. What does `STATELESS` mean?
12. Why is the JWT included in the Authorization header?
13. What happens when a JWT expires?
14. What happens if someone modifies the JWT payload?
15. What is the difference between a JWT signature and encryption?
16. Why should passwords never be included in a JWT?
17. What is the purpose of DTOs?
18. Why do we separate controllers, services, and repositories?
19. What is the difference between a Spring bean and an ordinary Java object?
20. What happens when a request reaches a protected endpoint?

## 13. Final Summary

Remember the responsibility of each major component:

- **Controller:** Handles HTTP requests and responses.
- **Service:** Implements business logic.
- **Repository:** Reads and writes database data.
- **Entity:** Represents persistent data.
- **DTO:** Defines API request and response structures.
- **PasswordEncoder:** Hashes and verifies passwords.
- **UserDetailsService:** Loads user details for Spring Security.
- **AuthenticationManager:** Coordinates authentication.
- **DaoAuthenticationProvider:** Performs database-backed credential verification.
- **JwtService:** Creates and validates JWTs.
- **JwtAuthenticationFilter:** Extracts a token and establishes authentication.
- **SecurityContextHolder:** Holds the current request's security context.
- **SecurityFilterChain:** Configures the security filters and access rules.

The most important execution flow to remember is:

**Register → Encode password → Save user → Login → Verify credentials → Generate JWT → Send JWT → Validate JWT → Establish authentication → Access protected API.**
