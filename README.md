# QA Testing Kit — Kiro Power 🚀

> Bộ Kit QA Testing toàn diện cho Manual & Automation Testing — Kiro Power format. Hỗ trợ Playwright, Selenium, Appium với MCP server cho Jira/Xray integration.

## Cài đặt

1. Copy thư mục `qa-testing-kit/` vào Kiro Powers directory
2. Power sẽ tự động được nhận diện qua keywords

## Cấu trúc

```
qa-testing-kit/
├── POWER.md                                    # Main documentation + global rules
├── mcp.json                                    # MCP server config (Jira/Xray)
├── README.md                                   # Hướng dẫn cài đặt
├── mcp-server/                                 # MCP server source
│   ├── index.js
│   └── package.json
└── steering/                                   # Guided workflows & rules
    ├── qa-contract.md                          # Core contract (paths, lifecycle, behavior)
    ├── qa-routing.md                           # NLP intent → command dispatch
    ├── qa-skill-next.md                        # State-aware next step detection
    ├── qa-impact.md                            # Change impact analysis
    ├── qa-token-budget.md                      # Context budget guardrails
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

## Tác giả

**TDFS-Dom** — https://github.com/TDFS-Dom

## Repository

```
https://github.com/TDFS-Dom/qa-testing-kit
```

## License

MIT
