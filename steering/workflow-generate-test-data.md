# Workflow: Sinh Test Data Có Cấu Trúc

> Sinh test data unique, traceable cho automation tests — bao gồm positive, negative, boundary và edge cases.

## Quy tắc Test Data

Tất cả data phải:
- **Unique** — Không trùng lặp trong test suite
- **Traceable** — Biết test nào tạo ra data nào
- **Deterministic** — Cùng seed → cùng data (khi cần)

## Format khuyến nghị

```
<prefix>_<testName>_<timestamp>
```

Ví dụ:
- Email: `auto_register_20260402@test.com`
- Username: `user_login_20260402133000`
- Code: `TC_REG_1712049200`

## Data Categories

### Positive Data (Happy Path)
- Valid format, within constraints
- All required fields filled
- Standard business values

### Negative Data
- Missing required fields
- Invalid format (wrong email, short password)
- Invalid characters
- Already existing values (duplicate check)

### Boundary Values
- Minimum length (1 character)
- Maximum length (255 characters)
- Min + 1, Max - 1
- Empty string vs null
- Zero, negative numbers

### Edge Cases
- Unicode / special characters
- Very long strings
- SQL injection patterns (security testing)
- HTML tags in text fields
- Leading/trailing whitespace

## Output Format

```json
{
  "positive": [
    { "email": "auto_tc01_20260402@test.com", "password": "Test@12345" }
  ],
  "negative": [
    { "email": "", "password": "Test@12345", "expectedError": "Email is required" }
  ],
  "boundary": [
    { "email": "a@b.co", "password": "12345678", "note": "Min length" }
  ]
}
```

## Multi-Step Data Pipeline (Cross-Module)

Khi test data cần đi qua nhiều modules nối tiếp:

```
Module 1 → Output: {id_1, code_1}
    ↓ (Reference)
Module 2 → Input: {id_1} → Output: {id_2}
    ↓
Module N → Input: {id_1..id_N-1} → Final Result
```

### Field Classification

| Loại | Mô tả | Cách sinh |
|---|---|---|
| Dimension field | Giá trị thuộc chiều kết hợp | Lấy từ ma trận — KHÔNG random |
| Supporting field | Bắt buộc nhưng không phải dimension | Random + unique + traceable |
| Reference field | ID/code từ module trước | Copy từ output module trước |
| Computed field | Tự tính từ formula | Tính theo formula — verify |

### Tracing Format

```
auto_combo{XX}_{module_short}_{timestamp}
```

## Constraints

- Respect field validation rules (từ DOM inspection)
- Match input format (date, phone format)
- Avoid duplication across test runs
- Not contain real PII
