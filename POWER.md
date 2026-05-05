---
name: "qa-testing-kit"
displayName: "QA Testing Kit"
description: "Bộ Kit QA Testing toàn diện cho Manual & Automation Testing — hỗ trợ Playwright, Selenium, Appium. Sinh test cases, automation scripts, locators, test data, và phân tích flaky tests."
keywords: ["testing", "qa", "automation", "playwright", "selenium", "appium", "test cases", "manual testing", "locator", "pom", "page object", "test data", "flaky tests", "api testing", "rbt", "framework", "testng", "jira", "xray"]
author: "TDFS-Dom"
---

# QA Testing Kit — Kiro Power

> **Bộ Kit QA Testing toàn diện** cho cả Manual & Automation Testing. Hỗ trợ Playwright, Selenium, Appium với MCP server cho Jira/Xray integration.

## Tổng quan

Power này cung cấp sẵn các quy tắc hành vi (Rules), kỹ năng (Skills), và quy trình (Workflows) để hỗ trợ AI Agent thực hiện toàn bộ vòng đời kiểm thử phần mềm:

- Phân tích yêu cầu (Requirements Analysis)
- Thiết kế test cases (Manual Testing — QUICK & FULL RBT)
- Sinh automation scripts (Playwright, Selenium, Appium)
- Sinh test data có cấu trúc
- Phân tích & sửa flaky tests
- Sinh locators ổn định
- Thiết kế automation framework
- Tích hợp Jira/Xray

## Available MCP Servers

Power này bao gồm MCP server `jira-xray` cho tích hợp Jira/Xray:

### Tools

| Tool | Mô tả |
|---|---|
| `fetch_jira_issue` | Lấy 1 Jira issue theo key (PROJ-123) → trả về markdown/json |
| `fetch_jira_issues` | Lấy nhiều issues theo project, type, hoặc JQL |
| `fetch_epic_children` | Lấy tất cả children của 1 Epic |
| `xray_authenticate` | Xác thực Xray Cloud API |
| `xray_import_results` | Import test results (Playwright/JUnit) lên Xray |
| `test_jira_connection` | Test kết nối đến Jira API |

### Environment Variables (cần cấu hình)

| Variable | Mô tả | Bắt buộc |
|---|---|---|
| `JIRA_BASE_URL` | URL Jira instance | ✅ |
| `JIRA_EMAIL` | Email tài khoản (Cloud) | Cloud |
| `JIRA_API_TOKEN` | API Token (Cloud) | Cloud |
| `JIRA_PAT` | Personal Access Token (Server/DC) | Server |
| `JIRA_PROJECT_KEY` | Project key mặc định | Khuyến nghị |
| `XRAY_PLATFORM` | `cloud` hoặc `server` | Mặc định: cloud |
| `XRAY_CLIENT_ID` | Xray Client ID | Xray Cloud |
| `XRAY_CLIENT_SECRET` | Xray Client Secret | Xray Cloud |

---

## When to Load Steering Files

Load steering file phù hợp dựa trên tác vụ user yêu cầu:

- Writing automation tests or reviewing automation code → `automation-rules.md`
- Finding or generating locators for UI elements → `locator-strategy.md`
- Working with Playwright framework → `playwright-rules.md`
- Working with Selenium framework → `selenium-rules.md`
- Working with Appium mobile automation → `appium-rules.md`
- Generating manual test cases quickly from requirements → `workflow-generate-testcases-quick.md`
- Generating manual test cases with full RBT 6-step process → `workflow-generate-manual-testcases-rbt.md`
- Converting manual test cases to automation scripts → `workflow-generate-automation.md`
- Generating requirements from website analysis → `workflow-generate-requirements.md`
- Designing or scaffolding automation framework → `workflow-generate-framework.md`
- Generating structured test data → `workflow-generate-test-data.md`
- Analyzing and fixing flaky tests → `workflow-analyze-flaky-tests.md`
- Generating stable locators for elements → `workflow-generate-locator.md`
- Generating API tests from Swagger/OpenAPI → `workflow-generate-api-tests.md`
- Analyzing cross-module features or combinatorial testing → `workflow-cross-module-testing.md`
- Integrating with Jira or pushing results to Xray → `workflow-jira-integration.md`

---

## Quy tắc toàn cục (Global Rules)

### 🔐 Security & Credentials (ƯU TIÊN CAO NHẤT)

- **CẤM** đọc file `.env` trực tiếp để lấy credentials
- **CẤM** in thông tin nhạy cảm (API Keys, Passwords, Tokens) ra chat
- **CẤM** commit file chứa credentials lên repository

### 🗣️ Ngôn ngữ & Giao tiếp

- Luôn giao tiếp, phân tích và báo cáo bằng **Tiếng Việt**
- Code comments có thể viết bằng Tiếng Anh
- Tên biến, hàm, class luôn viết bằng Tiếng Anh

### 🖥️ Browser Rules

- Viewport debug: **1920×1080** (desktop)
- Headed mode bắt buộc khi debug
- Headless chỉ khi test đã PASS hoặc trong CI/CD
- Thứ tự MCP: `navigate → resize(1920×1080) → wait → snapshot → interact → screenshot(on_fail)`

### 🏗️ Architecture

- Bắt buộc **Page Object Model (POM)**
- Phân tách: Page classes / Test classes / Test data
- Assertions chỉ trong Test classes

### ⏱️ Smart Waits (NGHIÊM CẤM hard sleep)

| Framework | Smart Wait |
|---|---|
| Playwright | `expect().toBeVisible()`, `expect().toBeEnabled()`, Locator APIs |
| Selenium | `WebDriverWait` + `ExpectedConditions` |
| Appium | `WebDriverWait` + custom conditions |

### 🧪 Test Data

- Tất cả field unique: **BẮT BUỘC** dùng random + traceable
- Format: `<prefix>_<testName>_<timestamp>`
- Ví dụ: `auto_login_1712049200@test.com`

### ✅ Definition of Done

Test chỉ hoàn thành khi:
- [ ] Xóa debug logs, commented code, unused locators
- [ ] Tuân thủ POM — locator trong Page class, không inline
- [ ] Test PASS ổn định 2 lần liên tiếp (headed mode)
- [ ] Assertion có message rõ ràng
- [ ] Test data unique + traceable
- [ ] Không có `waitForTimeout()` / `Thread.sleep()`

### ❌ Anti-Patterns (FORBIDDEN)

| Anti-Pattern | Thay thế đúng |
|---|---|
| Đoán locator | Inspect DOM thực tế |
| Hard sleep | Smart waits |
| Copy locator cũ không verify | Verify trên browser hiện tại |
| Viết test không chạy ngay | Chạy test ngay sau implement |
| Commit test FAIL | Chỉ commit khi PASS ổn định |
| Debug log khi deliver | Cleanup trước deliver |
| Test data hardcoded | Random + traceable |

---

## Tech Stack hỗ trợ

| Loại | Công nghệ |
|---|---|
| Ngôn ngữ | Java, TypeScript, Python |
| Web Automation | Playwright (TS/Java/Python), Selenium (Java/Python) |
| Mobile Automation | Appium (Java) |
| API Automation | REST Assured, Playwright API |
| Test Framework | TestNG, Playwright Test, Pytest |
| Build Tool | Maven, npm, pip |
| Reporting | Allure, HTML Report, ExtentReports |

---

## Workflow Routing

Khi user yêu cầu một tác vụ, chọn steering file phù hợp:

| User yêu cầu | Steering file |
|---|---|
| "sinh test cases nhanh", "tạo TC từ requirement" | `workflow-generate-testcases-quick.md` |
| "quy trình 6 bước", "sinh TC đầy đủ", "RBT" | `workflow-generate-manual-testcases-rbt.md` |
| "convert TC sang automation", "sinh Playwright/Selenium script" | `workflow-generate-automation.md` |
| "sinh requirements từ website" | `workflow-generate-requirements.md` |
| "tạo framework", "scaffold project" | `workflow-generate-framework.md` |
| "sinh test data", "boundary data" | `workflow-generate-test-data.md` |
| "test flaky", "test không ổn định" | `workflow-analyze-flaky-tests.md` |
| "sinh locator", "tìm selector" | `workflow-generate-locator.md` |
| "API test từ Swagger" | `workflow-generate-api-tests.md` |
| "cross-module", "ma trận kết hợp" | `workflow-cross-module-testing.md` |
| "lấy requirement từ Jira", "push Xray" | `workflow-jira-integration.md` |

---

## Cleanup Rules

Cuối mỗi nhiệm vụ, agent PHẢI:
1. Scan workspace tìm file tạm (`*_debug.txt`, `*.tmp`, `scratch_*`)
2. Xóa tất cả file tạm
3. Báo cáo cleanup summary

**KHÔNG xóa:** `playwright-report/`, `test-results/`, `logs/`, `node_modules/`, `.git/`, config files.

