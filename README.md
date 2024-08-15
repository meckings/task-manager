# Task Management API Documentation

## Table of Contents

1. [Overview](#overview)
2. [Setup](#setup)
3. [API Version](#api-version)
4. [Base URL](#base-url)
5. [Authentication](#authentication)
6. [API Documentation and Testing](#api-documentation-and-testing)
7. [Endpoints](#endpoints)
   - [User Controller](#user-controller)
   - [Task Controller](#task-controller)
   - [Role Controller](#role-controller) (Admin Access Required)
   - [Authentication Controller](#authentication-controller)
8. [Schemas](#schemas)
9. [Usage](#usage)
10. [Examples](#examples)
11. [Error Handling](#error-handling)

---

## Overview

The Task Management API is designed to manage tasks, users, roles, and their relationships within a system. It also includes endpoints for user authentication and role assignment.

---

## Setup

1. **Create a MySQL Database**:
  - Create a new MySQL database to be used with this application.

2. **Update `application.properties`**:
  - Replace the following values in the `application.properties` file:
    ```properties
    jdbc.database=your-database-name
    jdbc.user=your-database-username
    jdbc.password=your-database-password
    
    spring.mail.username=your-email@example.com
    spring.mail.password=your-email-password
    spring.mail.host=smtp-mail.outlook.com
    ```
    Ensure you replace `your-database-name`, `your-database-username`, `your-database-password`, `your-email@example.com`, and `your-email-password` with your actual values.

3. **Build and Run the Service**:
  - Build the project by running:
    ```bash
    mvn clean package
    ```
  - Run the project by executing:
    ```bash
    mvn spring-boot:run
    ```
### Admin User Account
After you run the project the first time, a default admin user account will be created. Only an admin can make other users an admin, so use this account if you want to make another user an admin.
The admin account is
```
email :   test@test.com
password: password
```

---

## API Version

- **Title**: Task Management
- **Version**: v1

## Base URL

- **Server URL**: `http://localhost:8080/task-management`

## Authentication

The API uses JWT (JSON Web Token) for securing endpoints. You need to include a Bearer token in the Authorization header of your requests to access secured endpoints.

### Security Scheme

- **Type**: HTTP
- **Scheme**: Bearer
- **Format**: JWT

### Example

```http
Authorization: Bearer <your-jwt-token>
```

---

## API Documentation and Testing

This project was set up with Swagger. You can view the documentation and call the APIs through the Swagger UI at:
```
{host}/swagger-ui/index.html
```

For a Postman collection, use:
```
https://api.postman.com/collections/4433833-4d3581eb-17fe-45e1-9a37-c653296e78cc?access_key=PMAT-01J5BQVV7MXJK6TDCSXHSG6YPS
```

---

## Endpoints

### User Controller

#### Assign Role to User

- **URL**: `/api/v1/user/role`
- **Method**: `PUT`
- **Query Parameters**:
  - `userId` (required, integer): ID of the user.
  - `role` (required, integer): ID of the role to be assigned to the user.
- **Access**: Admin

#### Register User

- **URL**: `/api/v1/user/register`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```

### Task Controller

#### Get Task by ID

- **URL**: `/api/v1/tasks`
- **Method**: `GET`
- **Query Parameters**:
  - `id` (required, integer): ID of the task.

#### Update Task

- **URL**: `/api/v1/tasks`
- **Method**: `PUT`
- **Request Body**:
  ```json
  {
    "id": "integer",
    "title": "string",
    "description": "string",
    "dueDate": "date",
    "status": "string",
    "priority": "integer",
    "assignedTo": "integer",
    "createdBy": "integer"
  }
  ```

#### Create Task

- **URL**: `/api/v1/tasks`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "title": "string",
    "description": "string",
    "dueDate": "date",
    "status": "string",
    "priority": "integer",
    "assignedTo": "integer",
    "createdBy": "integer"
  }
  ```

#### Delete Task

- **URL**: `/api/v1/tasks`
- **Method**: `DELETE`
- **Query Parameters**:
  - `id` (required, integer): ID of the task.

### Role Controller

#### Get Role by ID

- **URL**: `/api/v1/role`
- **Method**: `GET`
- **Query Parameters**:
  - `id` (required, integer): ID of the role.

#### Update Role

- **URL**: `/api/v1/role`
- **Method**: `PUT`
- **Access**:  Admin
- **Request Body**:
  ```json
  {
    "id": "integer",
    "name": "string"
  }
  ```

#### Register Role

- **URL**: `/api/v1/role`
- **Method**: `POST`
- **Access**:  Admin
- **Request Body**:
  ```json
  {
    "name": "string"
  }
  ```

### Authentication Controller

#### Get Token

- **URL**: `/api/v1/auth/token`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```

### User Controller

#### Get User by ID

- **URL**: `/api/v1/user`
- **Method**: `GET`
- **Query Parameters**:
  - `id` (required, integer): ID of the user.

### Task Search

#### Search Tasks

- **URL**: `/api/v1/tasks/search`
- **Method**: `GET`
- **Query Parameters**:
  - `page` (required, integer): Page number for pagination.
  - `size` (required, integer): Number of items per page.
  - `title` (optional, string): Title of the task.
  - `description` (optional, string): Description of the task.
  - `status` (optional, string): Status of the task. Possible values: TODO, IN_PROGRESS, DONE, CANCELLED.
  - `dueDate` (optional, string): Due date of the task in YYYY-MM-DD format.

## Schemas

### TaskDto

- **Required**: `description`, `dueDate`, `title`
- **Type**: `object`
- **Properties**:
  - `id` (integer, format: int64): ID of the task.
  - `createdOn` (string, format: date-time): Date and time when the task was created.
  - `updatedOn` (string, format: date-time): Date and time when the task was last updated.
  - `title` (string, maxLength: 255): Title of the task.
  - `description` (string, maxLength: 2000): Description of the task.
  - `status` (string, enum: [TODO, IN_PROGRESS, DONE, CANCELLED]): Status of the task.
  - `priority` (integer, minimum: 0): Priority level of the task.
  - `dueDate` (string, format: date): Due date of the task.
  - `assignedTo` (integer, format: int64): ID of the user assigned to the task.
  - `createdBy` (integer, format: int64): ID of the user who created the task.

### RoleDto

- **Required**: `name`
- **Type**: `object`
- **Properties**:
  - `id` (integer, format: int64): ID of the role.
  - `createdOn` (string, format: date-time): Date and time when the role was created.
  - `updatedOn` (string, format: date-time): Date and time when the role was last updated.
  - `name` (string): Name of the role.

### UserDto

- **Required**: `email`, `password`
- **Type**: `object`
- **Properties**:
  - `id` (integer, format: int64): ID of the user.
  - `createdOn` (string, format: date-time): Date and time when the user was created.
  - `updatedOn` (string, format: date-time): Date and time when the user was last updated.
  - `email` (string): Email of the user.
  - `password` (string): Password of the user.

### AuthenticationRequest

- **Required**: `email`, `password`
- **Type**: `object`
- **Properties**:
  - `email` (string): Email address for authentication.
  - `password` (string): Password for authentication.

### AuthenticationResponse

- **Type**: `object`
- **Properties**:
  - `accessToken` (string): JWT token.
  - `tokenType` (string): Type of the token.
  - `expiration` (dateTime, format: date-time): Expiration date and time of the token.

### Page

- **Type**: `object`
- **Properties**:
  - `pageNumber` (integer, format: int32): Page number of the current result set.
  - `totalElements` (integer, format: int64): Total number of elements in the result set.
  - `data` (array): Array of `Objects`.

## Usage

To get started with the API:

1. **Obtain a JWT Token**:
  - Send a POST request to `/api/v1/auth/token` with your email and password.
  - The response will include an `accessToken` which you will need for accessing secured endpoints.

2. **Making Requests**:
  - Include the obtained JWT token in the Authorization header of your requests.
  - For example: `Authorization: Bearer <your-jwt-token>`

3. **Explore Endpoints**:
  - Use the provided endpoints to manage users, roles, and tasks.
  - Refer to the provided endpoint documentation for request parameters and response details.

## Examples

### Get All Tasks

```http
GET /api/v1/tasks?page=1&size=10
```

### Create a New Task

```http
POST /api/v1/tasks
Content-Type: application/json

{
  "title": "New Task",
  "description": "Description of the new task",
  "dueDate": "2024-08-15",
  "status": "TODO",
  "priority": 1,
  "assignedTo": 123,
  "createdBy": 456
}
```

### Register a New User

```http
POST /api/v1/user/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securepassword"
}
```

For further details and more examples, refer to the endpoint-specific documentation.

## Error Handling

If you encounter errors, check the response status code and message for more details. Common status codes include:

- `200 OK` - Request was successful.
- `400 Bad Request` - Invalid request parameters.
- `401 Unauthorized` - Authentication required.
- `403 Forbidden` - Authorization required.
- `404 Not Found` - Resource not found.
- `500 Internal Server Error` - Server error occurred.

