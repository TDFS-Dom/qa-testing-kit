# QA Testing Kit — Contract

Core contract cho QA Testing Kit Power. File này được load TRƯỚC mọi steering file khác.

## Defaults

- Language: `vi` (Vietnamese)
- Communication: Tiếng Việt cho giải thích, Tiếng Anh cho code
- Architecture: Page Object Model (POM)
- Viewport: 1920×1080 (desktop debug)
- Browser: Headed mode khi debug, Headless khi CI/CD
- Wait strategy: Smart waits only (NGHIÊM CẤM hard sleep)
- Test data: Random + traceable (`<prefix>_<testName>_<timestamp>`)

## QA Lifecycle

```
requirements → test-plan → test-cases → automation → execution → report
```

| Phase | Command | Output |
|---|---|---|
| 1. Requirements | `requirements` | requirements.md |
| 2. Test Plan | `test-plan` | test-plan.md |
| 3. Test Cases | `test-cases` | test-cases.md (Markdown table) |
| 4. Automation | `automation` | Page Objects + Test classes |
| 5. Execution | `execution` | Test results (PASS/FAIL/SKIP) |
| 6. Report | `report` | Execution summary + artifacts |

## Paths

```yaml
project_root: qa-output/{slug}/
project_home: qa-output/{slug}/PROJECT.md
requirements: qa-output/{slug}/01_requirements/requirements.md
test_plan: qa-output/{slug}/02_test-plan/test-plan.md
test_cases: qa-output/{slug}/03_test-cases/{module_slug}/test-cases.md
automation_pages: qa-output/{slug}/04_automation/src/pages/
automation_tests: qa-output/{slug}/04_automation/src/tests/
automation_utils: qa-output/{slug}/04_automation/src/utils/
execution_results: qa-output/{slug}/05_execution/results.md
execution_task: qa-output/{slug}/05_execution/task.md
report: qa-output/{slug}/06_report/report.md
locator_collection: qa-output/{slug}/04_automation/locator-collection.md
project_memory: qa-output/{slug}/project-memory.md
```

## Commands & Prerequisites

| Command | Module Required | Requires | Outputs |
|---|---|---|---|
| requirements | No | URL hoặc tài liệu input | requirements.md |
| test-plan | No | requirements | test-plan.md |
| test-cases | Yes (module) | requirements hoặc test-plan | test-cases.md |
| automation | Yes (module) | test-cases + URL app | Page Objects + Tests |
| execution | Yes (module) | automation code | results.md, task.md |
| report | No | ít nhất 1 execution result | report.md |
| locator | No | URL app | locator-collection.md |
| framework | No | — (standalone) | Full project scaffold |
| flaky-analysis | No | test code + error logs | analysis + fix |
| test-data | Yes (module) | test-cases hoặc requirements | test-data.json |
| cross-module | No | requirements (nhiều modules) | matrix + data |
| jira-fetch | No | Jira credentials configured | requirements.md |
| xray-push | No | execution results + Xray credentials | import confirmation |
| impact | No | ít nhất requirements | (analysis only) |
| status | No | — | (inspection only) |
| next | No | — | (recommendation only) |

## Resolution Rules

### Slug
1. Prefer explicit `--slug` từ user
2. Otherwise inspect directories under `qa-output/`
3. If more than one slug → stop and ask

### Module
1. For module-required commands, prefer explicit `--module`
2. If exactly one module directory exists → use it
3. If multiple → stop and ask
4. Never infer from partial matches

## Behavior Rules

### Fail-Closed
- Never silently choose slug or module
- If prerequisite missing → print exact path + command to run, then stop
- If ambiguous → stop and ask

### Overwrite Protection
Before mutating test-cases, automation code, or reports:
1. Check if target output path exists
2. If exists → print path and ask whether to overwrite
3. If no explicit approval → stop

### Impact-First Rule
- Khi user yêu cầu thay đổi requirements hoặc test cases đã có → route qua `impact` trước
- Bare correction ("thêm field X vào form") = impact input
- Chỉ mutate sau khi user approve impact analysis

### Execution Lock
After user approves a mutating step:
- Keep that step locked for the rest of the run
- Do not reopen discovery
- Only break lock when scope becomes genuinely ambiguous

### Auto-Heal Rule (E3)
Khi test FAIL trong execution phase:
- Tự đọc log → phân tích → sửa → chạy lại (tối đa 5 vòng)
- CẤM hỏi user trong quá trình fix
- Chỉ hỏi khi: business rule mâu thuẫn, server inaccessible, hoặc hết 5 vòng

### Large Artifact Write Protocol
Khi generating artifacts >100 lines:
1. Write skeleton first (headings, structure)
2. Append content sequentially (module by module, TC by TC)
3. Never assemble full artifact in memory then flush

### Source of Truth Order
1. `requirements.md` (primary after scope lock)
2. `test-plan.md` (secondary)
3. User input (fallback)

### Next Step Order
`requirements → test-plan → test-cases → automation → execution → report`

## Security Rules (ƯU TIÊN CAO NHẤT)

- **CẤM** đọc file `.env` trực tiếp
- **CẤM** in credentials ra chat
- **CẤM** commit file chứa credentials
- Credentials chỉ qua environment variables

## Tech Stack

| Platform | Framework | Language | Runner |
|---|---|---|---|
| Web | Playwright | TypeScript | Playwright Test |
| Web | Playwright | Java | TestNG |
| Web | Playwright | Python | Pytest |
| Web | Selenium | Java | TestNG |
| Web | Selenium | Python | Pytest |
| Mobile | Appium | Java | TestNG |
| API | REST Assured | Java | TestNG |
| API | Playwright API | TypeScript | Playwright Test |

## Project Memory

File `project-memory.md` lưu trữ:
- Canonical vocabulary (tên module, tên field chuẩn)
- Approved decisions (tech stack, framework choice)
- Assumptions (khi user không trả lời Q&A)
- Corrections (thay đổi đã approve qua impact)
- Known issues (CAPTCHA, 2FA, external dependencies)

Cập nhật project-memory sau mỗi phase hoàn thành.
