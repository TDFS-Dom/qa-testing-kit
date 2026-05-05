---
name: "qa-testing-kit"
displayName: "QA Testing Kit"
description: "Bộ Kit QA Testing toàn diện cho Manual & Automation Testing — hỗ trợ Playwright, Selenium, Appium. Sinh test cases, automation scripts, locators, test data, và phân tích flaky tests."
keywords: ["testing", "qa", "automation", "playwright", "selenium", "appium", "test cases", "manual testing", "locator", "pom", "page object", "test data", "flaky tests", "api testing", "rbt", "framework", "testng", "jira", "xray"]
author: "TDFS-Dom"
---

# QA Testing Kit — Kiro Power

Bộ Kit QA Testing toàn diện cho cả Manual & Automation Testing. Hỗ trợ Playwright, Selenium, Appium với MCP server cho Jira/Xray integration.

## Overview

Power này cung cấp **lifecycle-driven QA workflow** — từ phân tích yêu cầu đến báo cáo kết quả, với state tracking và impact analysis.

**QA Lifecycle:**
```
requirements → test-plan → test-cases → automation → execution → report
```

**Key capabilities:**
- Phân tích yêu cầu (Requirements Analysis) từ website/Jira
- Thiết kế test cases (QUICK mode & FULL RBT 6 bước)
- Sinh automation scripts (Playwright, Selenium, Appium)
- Sinh test data có cấu trúc (positive, negative, boundary, combinatorial)
- Phân tích & sửa flaky tests
- Sinh locators ổn định
- Thiết kế automation framework
- Tích hợp Jira/Xray (fetch requirements, push results)
- State-aware continuation (biết đang ở đâu, recommend next step)
- Impact analysis (đánh giá ảnh hưởng khi thay đổi)

## Available MCP Servers

### jira-xray

**Connection:** `npx -y qa-testing-kit-jira-mcp@latest`

| Tool | Mô tả |
|---|---|
| `fetch_jira_issue` | Lấy 1 Jira issue theo key → markdown/json |
| `fetch_jira_issues` | Lấy nhiều issues (project/type/JQL) |
| `fetch_epic_children` | Lấy children của Epic |
| `xray_authenticate` | Xác thực Xray Cloud API |
| `xray_import_results` | Import test results (Playwright/JUnit) lên Xray |
| `test_jira_connection` | Test kết nối Jira API |

**Environment Variables:**

| Variable | Mô tả |
|---|---|
| `JIRA_BASE_URL` | URL Jira instance |
| `JIRA_EMAIL` | Email tài khoản (Cloud) |
| `JIRA_API_TOKEN` | API Token (Cloud) |
| `JIRA_PAT` | Personal Access Token (Server/DC) |
| `JIRA_PROJECT_KEY` | Project key mặc định |
| `XRAY_PLATFORM` | `cloud` hoặc `server` |
| `XRAY_CLIENT_ID` | Xray Client ID |
| `XRAY_CLIENT_SECRET` | Xray Client Secret |

## When to Load Steering Files

### Always-loaded (core contract + routing)
- Understanding QA lifecycle, paths, prerequisites, behavior rules → `qa-contract.md`
- Routing user intent to correct workflow → `qa-routing.md`

### On-demand (load per active command)
- Detecting next step in lifecycle → `qa-skill-next.md`
- Analyzing change impact before mutation → `qa-impact.md`
- Token budget management → `qa-token-budget.md`
- Writing automation tests → `automation-rules.md`
- Finding/generating locators → `locator-strategy.md`
- Working with Playwright → `playwright-rules.md`
- Working with Selenium → `selenium-rules.md`
- Working with Appium → `appium-rules.md`
- Generating test cases quickly → `workflow-generate-testcases-quick.md`
- Generating test cases with RBT 6-step → `workflow-generate-manual-testcases-rbt.md`
- Converting test cases to automation → `workflow-generate-automation.md`
- Generating requirements from website → `workflow-generate-requirements.md`
- Designing automation framework → `workflow-generate-framework.md`
- Generating test data → `workflow-generate-test-data.md`
- Analyzing flaky tests → `workflow-analyze-flaky-tests.md`
- Generating stable locators → `workflow-generate-locator.md`
- Generating API tests from Swagger → `workflow-generate-api-tests.md`
- Cross-module combinatorial testing → `workflow-cross-module-testing.md`
- Jira/Xray integration → `workflow-jira-integration.md`

## Quick Start

### Bắt đầu dự án QA mới
```
Tôi có URL website, hãy phân tích và sinh requirements
```

### Tiếp tục dự án
```
Tiếp tục dự án, bước tiếp theo là gì?
```

### Sinh test cases nhanh
```
Sinh test cases từ requirements này
```

### Sinh automation
```
Convert test cases sang Playwright automation
```

### Đánh giá thay đổi
```
Thêm validation cho field email — đánh giá ảnh hưởng
```

## Output Directory Structure

```
qa-output/{slug}/
├── PROJECT.md                          # Project overview + status
├── project-memory.md                   # Decisions, vocabulary, corrections
├── 01_requirements/
│   └── requirements.md
├── 02_test-plan/
│   └── test-plan.md
├── 03_test-cases/
│   ├── {module-a}/test-cases.md
│   └── {module-b}/test-cases.md
├── 04_automation/
│   ├── locator-collection.md
│   └── src/
│       ├── pages/
│       ├── tests/
│       └── utils/
├── 05_execution/
│   ├── task.md
│   └── results.md
└── 06_report/
    └── report.md
```

## Behavior Rules (Summary)

Full rules in `qa-contract.md`. Key points:

1. **Fail-Closed** — Never guess slug/module. Ask if ambiguous.
2. **Overwrite Protection** — Ask before mutating existing artifacts.
3. **Impact-First** — Changes to existing artifacts go through impact analysis first.
4. **Auto-Heal (E3)** — Test FAIL → self-fix loop (max 5 rounds), no user questions.
5. **Smart Waits Only** — NGHIÊM CẤM `Thread.sleep()` / `waitForTimeout()`.
6. **POM Mandatory** — Page Object Model for all automation.
7. **Traceable Data** — All test data: `<prefix>_<testName>_<timestamp>`.
8. **Security** — Never read .env, never print credentials.

## Tech Stack

| Platform | Framework | Language | Runner |
|---|---|---|---|
| Web | Playwright | TypeScript/Java/Python | Playwright Test/TestNG/Pytest |
| Web | Selenium | Java/Python | TestNG/Pytest |
| Mobile | Appium | Java | TestNG |
| API | REST Assured | Java | TestNG |
| API | Playwright API | TypeScript | Playwright Test |
