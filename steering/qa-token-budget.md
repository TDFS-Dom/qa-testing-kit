# QA Testing Kit — Token Budget

Guardrail cho context window. Kiểm soát steering file loading.

## Nguyên tắc

- Chỉ load steering files cần thiết cho command hiện tại
- Never load all steering files cùng lúc
- Always-loaded files phải nhỏ (<3KB mỗi file)
- On-demand files load 1-2 tại 1 thời điểm

## Always-Loaded (mỗi interaction)

| File | Est. size | Mục đích |
|---|---|---|
| `qa-contract.md` | ~4KB | Paths, prerequisites, behavior rules |
| `qa-routing.md` | ~3KB | NLP intent → command dispatch |

**Total always-loaded: ~7KB**

## On-Demand Load Bundles

| Command | Load files | Est. size |
|---|---|---|
| requirements | qa-contract + workflow-generate-requirements | ~7KB |
| test-cases QUICK | qa-contract + workflow-generate-testcases-quick | ~6KB |
| test-cases RBT | qa-contract + workflow-generate-manual-testcases-rbt | ~7KB |
| automation | qa-contract + workflow-generate-automation + automation-rules | ~12KB |
| framework | qa-contract + workflow-generate-framework | ~9KB |
| locator | qa-contract + workflow-generate-locator + locator-strategy | ~8KB |
| flaky-analysis | qa-contract + workflow-analyze-flaky-tests | ~6KB |
| test-data | qa-contract + workflow-generate-test-data | ~6KB |
| api-tests | qa-contract + workflow-generate-api-tests | ~6KB |
| cross-module | qa-contract + workflow-cross-module-testing | ~7KB |
| jira/xray | qa-contract + workflow-jira-integration | ~6KB |
| impact | qa-contract + qa-impact | ~7KB |
| next | qa-contract + qa-skill-next | ~5KB |
| Playwright-specific | + playwright-rules | +3KB |
| Selenium-specific | + selenium-rules | +3KB |
| Appium-specific | + appium-rules | +3KB |

## Framework-Specific Loading

Chỉ load framework rules khi command cần:
- `automation` + Playwright → load `playwright-rules.md`
- `automation` + Selenium → load `selenium-rules.md`
- `automation` + Appium → load `appium-rules.md`
- `locator` → load `locator-strategy.md` + framework-specific

## Khi budget tight

1. Không load framework rules nếu command không liên quan code
2. Không load locator-strategy nếu command là test-cases hoặc requirements
3. Tách workflow file nếu phình >200 lines
4. Dùng qa-contract summary thay vì full behavior rules
