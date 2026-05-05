# Workflow: Thiết Kế Automation Framework

> Thiết kế, scaffold và triển khai automation framework hoàn chỉnh từ đầu.

## Stacks hỗ trợ

| Platform | Stack | Ngôn ngữ | Runner | Report |
|---|---|---|---|---|
| Web | Playwright | TypeScript | Playwright Test | HTML Report, Allure |
| Web | Playwright | Java | TestNG/JUnit5 | Allure |
| Web | Playwright | Python | Pytest | pytest-html, Allure |
| Web | Selenium | Java | TestNG | Allure, ExtentReports |
| Mobile | Appium | Java | TestNG | Allure, ExtentReports |
| API | REST Assured | Java | TestNG | Allure |
| API | Playwright API | TypeScript | Playwright Test | HTML Report |

## Các bước thực hiện

### Bước 1: Thu thập yêu cầu (⏸️ CHECKPOINT)

Hỏi user:
- Platform? (Web / Mobile / API / Hybrid)
- Framework? (Playwright / Selenium / Appium)
- Ngôn ngữ? (TypeScript / Java / Python)
- Project name?
- CI/CD? (GitHub Actions / GitLab CI / Jenkins)
- Reporting tool?

**Xác nhận với user trước khi scaffold.**

### Bước 2: Scaffold Project Structure

Tạo thư mục + file cấu hình gốc theo stack:
- `package.json` / `pom.xml` / `requirements.txt`
- `playwright.config.ts` / `testng.xml` / `conftest.py`
- `.env.example`, `.gitignore`, `README.md`

### Bước 3: Sinh Core Classes

- **Configuration Management** — đọc .env, typed config
- **Browser/Driver Management** — Factory pattern, thread-safe
- **Base Page/Screen** — common methods, smart waits, screenshot on failure
- **Base Test** — setup/teardown, lifecycle hooks
- **Utilities** — TestDataGenerator, WaitHelper, ScreenshotUtil, Logger

### Bước 4: Sinh Example Tests

- 1 example Page Object (LoginPage)
- 1 example Test (LoginTest) — Arrange → Act → Assert
- 1 data-driven test (nếu phù hợp)

### Bước 5: Reporting & CI/CD

- Tích hợp reporting tool (Allure / HTML Report)
- Screenshot auto-attach on failure
- CI/CD pipeline template (nếu yêu cầu)

### Bước 6: Verify & Deliver

- Build/compile thành công
- Example test chạy được
- Review checklist: POM, smart waits, no hardcode, README đầy đủ

## Design Principles

1. **DRY** — Mỗi logic chỉ viết 1 lần
2. **Single Responsibility** — Mỗi class làm 1 việc
3. **Configuration over Code** — Env, browser, timeout qua config
4. **Fail Fast, Log Rich** — Screenshot on failure, structured logging

## Anti-Patterns

| ❌ Sai | ✅ Đúng |
|---|---|
| Hardcode URL/credentials | Đọc từ .env/config |
| Locator inline trong test | Khai báo trong Page class |
| `Thread.sleep()` | Smart waits |
| Global mutable state | Isolated fixtures per test |
| Monolithic test file | Tách theo module/feature |
