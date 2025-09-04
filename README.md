# Complete-custom-auth
Spring security best approach, clean and scalable approach

![Build Status](repo-diagram.svg)

# Complete Custom Auth — Spring Boot JWT Security

A **clean and scalable** Spring Boot project demonstrating a **complete custom authentication system** with:

- JWT-based access and refresh token flows
- Secure REST endpoints with custom filters
- Event-driven design using Spring’s Observer pattern (`@EventListener`)

---

##  Features

###  Authentication & Authorization with JWT
- `POST /login`: Issues **Access Token** (via HTTP response header) and **Refresh Token** (HttpOnly cookie) on successful login.
- `POST /api/v1/users/refresh`: Accepts the refresh token cookie, rotates tokens, and issues a new Access Token.

###  Token Validation & Security Flow
- **JWTAuthenticationFilter** validates tokens and handles expired/invalid tokens centrally with `AuthenticationEntryPoint` (returns 401 JSON).
- **AccessDeniedHandler** delivers 403 responses when authenticated users lack required authority.
- Optionally bypass token validation for refresh endpoint via `shouldNotFilter()` and a `permitAll()` rule in security configuration.

###  Exception Handling
- REST-centric error handling:
    - Login failures (e.g. unknown username or invalid credentials) are caught in the controller and return `401 Unauthorized`.
    - Other authentication failures are handled by Spring Security’s custom `AuthenticationEntryPoint` so controllers aren’t involved.

###  Observer Pattern via Spring Events
- Demonstrated using a simple publisher/listener setup: e.g.  using `AuthenticationSuccessEvent for decoupled logic.

###  CORS and Request Flow
- Configured CORS to allow REST clients to access APIs securely.
- Ensures preflight `OPTIONS` requests bypass security validation where needed to prevent unwanted 403s.

---

##  Project Structure
    src/main/java/com/yourapp/

    ├── config/ # Spring Security and CORS configurations
    ├── controller/ # Login & Refresh endpoints
    ├── dto/ # Request/Response payloads (AuthRequestDTO, JwtResponseDTO)
    ├── filter/ # JWTAuthenticationFilter (validates tokens)
    ├── handler/ # CustomEntryPoint & AccessDeniedHandler
    ├── service/ # UserDetailsService, RefreshTokenService, JwtUtil
    ├── event/ # Event publisher/listener examples
    └── exception/ # (Optional) Custom exceptions


---

##  How It Works

1. **Login Flow:**
    - Client calls `/login` with credentials.
    - On success, controller authenticates, generates JWTs, sets response header and cookie.

2. **Token Refresh Flow:**
    - Client sends `POST /refresh` with refresh token cookie.
    - Filter skips validation for this endpoint (`shouldNotFilter()`).
    - New Access + Refresh tokens are issued; old refresh token invalidated.

3. **Accessing Protected Endpoints:**
    - JWT filter validates the access token from the `Authorization` header.
    - If valid, sets `SecurityContext`; otherwise, triggers the custom entry point (401).

4. **Observer Example:**
    - A service publishes an event (e.g., AuthenticationSuccessEvent).
    - A listener logs or commits into log data without tight coupling.

---

##  Getting Started

```bash
git clone https://github.com/Saurabh1374/Complete-custom-auth.git
cd Complete-custom-auth
./mvnw clean package
java -jar target/complete-custom-auth-0.0.1-SNAPSHOT.jar
