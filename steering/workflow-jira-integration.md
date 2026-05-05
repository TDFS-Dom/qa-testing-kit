# Workflow: Tích Hợp Jira & Xray (via MCP)

> Sử dụng MCP server `jira-xray` để lấy requirements từ Jira và đẩy kết quả test lên Xray.

## Available MCP Tools

| Tool | Mô tả |
|---|---|
| `fetch_jira_issue` | Lấy 1 issue theo key → markdown requirement |
| `fetch_jira_issues` | Lấy nhiều issues (project/type/JQL) |
| `fetch_epic_children` | Lấy children của Epic |
| `xray_authenticate` | Xác thực Xray Cloud |
| `xray_import_results` | Push test results lên Xray |
| `test_jira_connection` | Kiểm tra kết nối Jira |

## Workflow: Fetch Requirements từ Jira

### 1. Test connection trước

Gọi tool `test_jira_connection` để verify credentials hoạt động.

### 2. Fetch issue(s)

**Lấy 1 issue cụ thể:**
```
Tool: fetch_jira_issue
Args: { "issue_key": "PROJ-123", "format": "md" }
```

**Lấy theo project + type:**
```
Tool: fetch_jira_issues
Args: { "project": "PROJ", "issue_type": "Story", "max_results": 20, "format": "md" }
```

**Tìm theo JQL:**
```
Tool: fetch_jira_issues
Args: { "jql": "project = PROJ AND status = 'To Do'", "format": "md" }
```

**Lấy children của Epic:**
```
Tool: fetch_epic_children
Args: { "epic_key": "PROJ-10", "format": "md" }
```

### 3. Lưu requirement

Sau khi fetch, lưu output vào:
```
practices/requirements/jira/{EPIC_KEY}/{ISSUE_KEY}/
├── {ISSUE_KEY}_requirement.md
└── (screenshots nếu có)
```

## Workflow: Push Test Results lên Xray

### 1. Authenticate (Xray Cloud)

```
Tool: xray_authenticate
```

### 2. Import results

**Playwright JSON:**
```
Tool: xray_import_results
Args: {
  "format": "playwright",
  "results_json": "<nội dung test-results.json>",
  "project_key": "PROJ",
  "test_plan_key": "PROJ-50"  // optional
}
```

**JUnit XML:**
```
Tool: xray_import_results
Args: {
  "format": "junit",
  "results_json": "<nội dung junit-results.xml>",
  "project_key": "PROJ"
}
```

## Test Key Convention

Đặt test key trong title để Xray mapping đúng test case:
```typescript
test('[PROJ-123] Login should work', async ({ page }) => { ... });
```

## Environment Variables (Prerequisites)

Cần cấu hình trước khi dùng MCP tools:

| Variable | Mô tả |
|---|---|
| `JIRA_BASE_URL` | URL Jira (e.g., `https://domain.atlassian.net`) |
| `JIRA_EMAIL` + `JIRA_API_TOKEN` | Auth cho Jira Cloud |
| `JIRA_PAT` | Auth cho Jira Server/DC |
| `XRAY_CLIENT_ID` + `XRAY_CLIENT_SECRET` | Auth cho Xray Cloud |

### Cách lấy Jira API Token (Cloud)

1. Truy cập https://id.atlassian.com/manage-profile/security/api-tokens
2. Create API token → copy → set env var `JIRA_API_TOKEN`

### Cách lấy Xray Cloud API Key

1. Truy cập Jira → Apps → Xray → Settings → API Keys
2. Tạo API Key → copy Client ID + Client Secret

## Troubleshooting

| Lỗi | Nguyên nhân | Giải pháp |
|---|---|---|
| HTTP 401 | Token sai | Kiểm tra JIRA_API_TOKEN hoặc JIRA_PAT |
| HTTP 403 | Không có quyền | Kiểm tra permission trên project |
| HTTP 404 | URL sai / issue không tồn tại | Kiểm tra JIRA_BASE_URL |
| `ENOTFOUND` | DNS không resolve | Kiểm tra domain |

## Lưu ý bảo mật

- **KHÔNG** commit credentials vào repo
- **KHÔNG** đọc .env trực tiếp trong agent
- Credentials qua environment variables trong mcp.json
