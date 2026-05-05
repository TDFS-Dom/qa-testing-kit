# Workflow: Sinh API Tests từ Swagger/OpenAPI

> Sinh automation tests cho REST API dựa trên Swagger/OpenAPI specification.

## Input

- Swagger URL hoặc OpenAPI spec file (JSON/YAML)
- Base URL của API
- Authentication method (Bearer token, API key, Basic auth...)

## Các bước thực hiện

### 1. Parse Swagger Spec

- Đọc và parse OpenAPI specification
- Liệt kê tất cả endpoints: method, path, parameters, request body, responses
- Xác định authentication scheme

### 2. Phân loại Endpoints

| Loại | Ví dụ | Test Strategy |
|---|---|---|
| CRUD | GET/POST/PUT/DELETE /users | Full CRUD cycle |
| Auth | POST /login, /register | Token flow, invalid credentials |
| Search/Filter | GET /users?name=X | Query params, pagination |
| File | POST /upload | File types, size limits |

### 3. Sinh Test Cases cho mỗi Endpoint

Với mỗi endpoint, sinh:

**Positive tests:**
- Happy path với valid data
- Verify response status code, body structure, headers

**Negative tests:**
- Missing required fields → 400
- Invalid data format → 400
- Unauthorized → 401
- Forbidden → 403
- Not found → 404

**Boundary tests:**
- Min/max length cho string fields
- Min/max value cho number fields
- Empty arrays, null values

### 4. Sinh Automation Code

**REST Assured (Java):**
```java
@Test
public void testCreateUserSuccess() {
    given()
        .header("Authorization", "Bearer " + token)
        .contentType(ContentType.JSON)
        .body(requestBody)
    .when()
        .post("/api/users")
    .then()
        .statusCode(201)
        .body("name", equalTo("Test User"));
}
```

**Playwright API (TypeScript):**
```typescript
test('create user successfully', async ({ request }) => {
    const response = await request.post('/api/users', {
        headers: { Authorization: `Bearer ${token}` },
        data: { name: 'Test User', email: 'test@example.com' }
    });
    expect(response.status()).toBe(201);
    const body = await response.json();
    expect(body.name).toBe('Test User');
});
```

### 5. Verify & Deliver

- Chạy tests against actual API
- Verify response schemas match spec
- Report: endpoints covered, pass/fail summary

## Output

- API test classes (organized by endpoint/module)
- Request/response models (DTOs)
- Test data fixtures
- Authentication helpers
- Execution report
