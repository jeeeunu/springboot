# Java Spring JPA Portfolio Project

This project is a basic implementation of a RESTful API for user authentication, user management, and post management using Spring Boot, Spring Security, and JPA.

## Features

- User registration and authentication
- User management (CRUD operations)
- Post management (CRUD operations)
- Role-based authorization
- RESTful API endpoints

## Technologies Used

- Java 22
- Spring Boot 3.3.13
- Spring Security
- Spring Data JPA
- PostgreSQL
- Lombok
- Validation

## Prerequisites

- Java 22 or higher
- PostgreSQL
- Maven or Gradle

## Setup

1. Clone the repository
2. Create a PostgreSQL database named `javaspring`
3. Update the database configuration in `src/main/resources/application.properties` if needed
4. Run the application using `./gradlew bootRun`

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get user details

### Users

- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/me` - Get current user
- `GET /api/users/{username}` - Get user by username
- `DELETE /api/users/{username}` - Delete user (Admin or self)

### Posts

- `GET /api/posts` - Get all posts
- `GET /api/posts/search?query={query}` - Search posts
- `GET /api/posts/user/{username}` - Get posts by user
- `GET /api/posts/{id}` - Get post by ID
- `POST /api/posts` - Create a new post (Authenticated)
- `PUT /api/posts/{id}` - Update post (Author only)
- `DELETE /api/posts/{id}` - Delete post (Author only)

## Testing

You can test the API using tools like Postman or curl.

### Example: Register a User

```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123",
  "name": "Test User",
  "email": "test@example.com"
}
```

### Example: Create a Post

```
POST /api/posts
Content-Type: application/json
Authorization: Basic dGVzdHVzZXI6cGFzc3dvcmQxMjM=

{
  "title": "My First Post",
  "content": "This is the content of my first post."
}
```

## License

This project is open source and available under the MIT License.