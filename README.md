# QA Testing Kit — Kiro Power 🚀

> Bộ Kit QA Testing toàn diện cho Manual & Automation Testing — Kiro Power format. Hỗ trợ Playwright, Selenium, Appium với MCP server cho Jira/Xray integration.

## Cài đặt

1. Copy thư mục `qa-testing-kit/` vào Kiro Powers directory
2. Power sẽ tự động được nhận diện qua keywords

## Cấu trúc

```
qa-testing-kit/
├── POWER.md                                    # Main documentation + global rules
├── package.json                                # Power metadata + keywords
├── README.md                                   # Hướng dẫn cài đặt
└── steering/                                   # Guided workflows & rules
    ├── automation-rules.md                     # Quy tắc chung automation (POM, naming...)
    ├── locator-strategy.md                     # Chiến lược chọn locator
    ├── playwright-rules.md                     # Quy tắc Playwright
    ├── selenium-rules.md                       # Quy tắc Selenium
    ├── appium-rules.md                         # Quy tắc Appium
    ├── workflow-generate-testcases-quick.md     # Sinh TC nhanh (QUICK mode)
    ├── workflow-generate-manual-testcases-rbt.md # Sinh TC đầy đủ (FULL RBT 6 bước)
    ├── workflow-generate-automation.md          # Sinh automation scripts
    ├── workflow-generate-requirements.md        # Sinh requirements từ website
    ├── workflow-generate-framework.md           # Thiết kế automation framework
    ├── workflow-generate-test-data.md           # Sinh test data
    ├── workflow-analyze-flaky-tests.md          # Phân tích flaky tests
    ├── workflow-generate-locator.md             # Sinh locator ổn định
    ├── workflow-generate-api-tests.md           # Sinh API tests từ Swagger
    ├── workflow-cross-module-testing.md         # Cross-module & ma trận kết hợp
    └── workflow-jira-integration.md            # Tích hợp Jira/Xray
```

## Keywords (Kích hoạt Power)

Power này được kích hoạt khi user nhắc đến:
- testing, qa, automation, playwright, selenium, appium
- test cases, manual testing, locator, pom, page object
- test data, flaky tests, api testing, rbt, risk based testing
- framework, testng, jira, xray

## Mapping từ Antigravity → Kiro Power

| Antigravity (cũ) | Kiro Power (mới) |
|---|---|
| `.agent/rules/*` | `steering/automation-rules.md`, `steering/*-rules.md` |
| `.agent/skills/qa_automation_engineer/` | `POWER.md` (workflow routing) |
| `.agent/skills/rbt_manual_testing/` | `steering/workflow-generate-manual-testcases-rbt.md` + `workflow-generate-testcases-quick.md` |
| `.agent/skills/requirements_analyzer/` | `steering/workflow-generate-requirements.md` |
| `.agent/skills/ui_debug_agent/` | Integrated into `POWER.md` + `steering/playwright-rules.md` |
| `.agent/skills/smart_locator_agent/` | `steering/workflow-generate-locator.md` |
| `.agent/skills/locator_healer_agent/` | `steering/workflow-generate-locator.md` (Healing section) |
| `.agent/skills/test_data_generator/` | `steering/workflow-generate-test-data.md` |
| `.agent/skills/flaky_test_analyzer/` | `steering/workflow-analyze-flaky-tests.md` |
| `.agent/skills/framework_architect/` | `steering/workflow-generate-framework.md` |
| `.agent/skills/jira_integration/` | `steering/workflow-jira-integration.md` |
| `.agent/workflows/*` | `steering/workflow-*.md` |
| `GEMINI.md` + `RULE_GLOBAL.md` | `POWER.md` (Global Rules section) |
| `plans/manual/` | `steering/workflow-generate-manual-testcases-rbt.md` |
| `plans/automation/` | `steering/workflow-generate-automation.md` + `workflow-generate-framework.md` |
| `plans/cross-module/` | `steering/workflow-cross-module-testing.md` |

## Tác giả

**TDFS-Dom** — https://github.com/TDFS-Dom

## Repository

```
https://github.com/TDFS-Dom/qa-testing-kit
```

## License

MIT
