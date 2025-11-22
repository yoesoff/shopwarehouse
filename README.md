Add a "Run with Docker" section at the top of your `README.md` to guide users on running the app using Docker Compose. Here’s the updated content:

```markdown
# Auth Service

## Run with Docker

docker-compose up --build
```

- The API will be available at: http://localhost:8080
- PostgreSQL will be available at: localhost:5433

## Build & Run (Locally)

```sh
mvn clean install
mvn spring-boot:run
```

## Default Users

| Username    | Email                 | Password     | Roles             |
|-------------|-----------------------|--------------|-------------------|
| admin       | admin@example.com     | password123  | ADMIN             |
| user1       | user1@example.com     | password123  | USER              |
| user2       | user2@example.com     | password123  | USER              |
| moderator   | moderator@example.com | password123  | MODERATOR         |
| superadmin  | superadmin@example.com| password123  | ADMIN, MODERATOR  |

## API Endpoints

### Register

- **POST** `/api/auth/register`
- **Request Body:**
  ```json
  {
    "username": "yourUsername",
    "email": "your@email.com",
    "password": "yourPassword"
  }
  ```
- **Response:** `201 Created` with a success message.

### Login

- **POST** `/api/auth/login`
- **Request Body:**
  ```json
  {
    "usernameOrEmail": "yourUsernameOrEmail",
    "password": "yourPassword"
  }
  ```
- **Response:** `200 OK` with:
  ```json
  {
    "accessToken": "jwt-token",
    "tokenType": "Bearer"
  }
  ```

## Requirements

- Java 21
- Maven
- PostgreSQL
```
This provides clear instructions for running the app with Docker at the top of the README.