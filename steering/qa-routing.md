# QA Testing Kit — Routing

Khi user dùng ngôn ngữ tự nhiên, route intent theo bảng sau. Đây là dispatcher — KHÔNG tự làm downstream work.

## Routing Table

| User nói... | Route to | Load steering |
|---|---|---|
| Sinh requirements từ website / phân tích module | `requirements` | `workflow-generate-requirements.md` |
| Sinh test plan / lập kế hoạch test | `test-plan` | `workflow-generate-manual-testcases-rbt.md` (Bước 1-4) |
| Sinh test cases nhanh / tạo TC | `test-cases` (QUICK) | `workflow-generate-testcases-quick.md` |
| Sinh TC đầy đủ / quy trình 6 bước / RBT | `test-cases` (FULL RBT) | `workflow-generate-manual-testcases-rbt.md` |
| Convert TC sang automation / sinh script | `automation` | `workflow-generate-automation.md` |
| Tạo framework / scaffold project | `framework` | `workflow-generate-framework.md` |
| Sinh test data / boundary data | `test-data` | `workflow-generate-test-data.md` |
| Sinh locator / tìm selector | `locator` | `workflow-generate-locator.md` |
| Test flaky / test không ổn định | `flaky-analysis` | `workflow-analyze-flaky-tests.md` |
| API test từ Swagger | `api-tests` | `workflow-generate-api-tests.md` |
| Cross-module / ma trận kết hợp | `cross-module` | `workflow-cross-module-testing.md` |
| Lấy requirement từ Jira | `jira-fetch` | `workflow-jira-integration.md` |
| Push kết quả lên Xray | `xray-push` | `workflow-jira-integration.md` |
| Thay đổi requirement / sửa TC đã có | `impact` | `qa-contract.md` (Impact-First Rule) |
| Tiếp tục / bước tiếp theo | `next` | Inspect artifacts → recommend |
| Kiểm tra trạng thái | `status` | Inspect `qa-output/{slug}/` |
| Chạy test / execute | `execution` | `workflow-generate-automation.md` (Bước 6) |
| Báo cáo kết quả | `report` | Generate summary |

## Routing Priority

Khi ambiguous:
- **Prefer `impact`** nếu user đề cập thay đổi artifact đã tồn tại
- **Prefer `test-cases` QUICK** nếu user nói "sinh TC" mà không nói "6 bước" hay "RBT"
- **Prefer `automation`** nếu user nói "Playwright" hoặc "Selenium" kèm "test"

## Natural Language Aliases

| User nói | Agent hiểu |
|---|---|
| Tạo TC / viết test cases | test-cases (QUICK) |
| Quy trình 6 bước / RBT / phân tích rủi ro | test-cases (FULL RBT) |
| Viết automation / convert sang code | automation |
| Dựng framework / scaffold | framework |
| Tìm locator / inspect UI | locator |
| Test hay fail / flaky | flaky-analysis |
| Sinh data / test data | test-data |
| Lấy từ Jira / fetch ticket | jira-fetch |
| Đẩy lên Xray / import results | xray-push |
| Tiếp tục / next | next |
| Trạng thái / status | status |
| Thay đổi / sửa / thêm yêu cầu | impact |

## Display Format

After routing, show:
```
🧪 QA-kit Routing
Input: {short excerpt}
→ Route: {command}
→ Load: {steering file}
→ Reason: {one-line reason}
```

Then load the steering file and execute.

## State-Aware Continuation

Khi user nói "tiếp tục" hoặc "bước tiếp theo":

1. Inspect `qa-output/{slug}/` directory
2. Check which artifacts exist:
   - `01_requirements/` exists? → requirements done
   - `02_test-plan/` exists? → test-plan done
   - `03_test-cases/` exists? → test-cases done
   - `04_automation/` exists? → automation done
   - `05_execution/` exists? → execution done
3. Recommend next phase in lifecycle order
4. Show current status + recommendation

```
📊 Project Status: {slug}
✅ Requirements — done
✅ Test Cases — done (3 modules)
⏳ Automation — in progress (1/3 modules)
⬚ Execution — pending
⬚ Report — pending

→ Recommended: Tiếp tục automation cho module {next_module}
```
