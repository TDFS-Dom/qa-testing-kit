# QA Testing Kit — Skill: Next Step Detection

State-aware recommendation engine. Inspect artifacts đã có → recommend bước tiếp theo.

## Invocation

Khi user nói: "tiếp tục", "bước tiếp theo", "next", "làm gì tiếp"

## Algorithm

```
1. Resolve slug (from context or ask user)
2. Scan qa-output/{slug}/ directory
3. Build state map:
   - requirements: exists? complete?
   - test-plan: exists?
   - test-cases: exists? which modules?
   - automation: exists? which modules? PASS/FAIL?
   - execution: exists? results?
   - report: exists?
4. Find first incomplete phase in lifecycle order
5. If phase partially done (some modules) → recommend next module
6. Present recommendation with context
```

## Lifecycle Order

```
requirements → test-plan → test-cases → automation → execution → report
```

## State Detection

| Check | How |
|---|---|
| Requirements done | `01_requirements/requirements.md` exists and non-empty |
| Test plan done | `02_test-plan/test-plan.md` exists |
| Test cases done | `03_test-cases/{module}/test-cases.md` exists for ALL modules in requirements |
| Automation done | `04_automation/src/tests/` has spec files matching test-cases modules |
| Execution done | `05_execution/results.md` exists with PASS/FAIL data |
| Report done | `06_report/report.md` exists |

## Module Tracking

Khi requirements define N modules:
- Track completion per module for test-cases and automation
- Recommend next incomplete module

## Output Format

```
📊 Project: {slug}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Phase              | Status
-------------------|--------
1. Requirements    | ✅ Done
2. Test Plan       | ✅ Done
3. Test Cases      | ⏳ 2/4 modules
   - auth          | ✅
   - users         | ✅
   - orders        | ⬚ pending
   - payments      | ⬚ pending
4. Automation      | ⬚ Pending
5. Execution       | ⬚ Pending
6. Report          | ⬚ Pending

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
→ Next: Sinh test cases cho module "orders"
→ Command: Load `workflow-generate-testcases-quick.md`
→ Input needed: requirements.md (section Orders)
```

## Edge Cases

| Situation | Action |
|---|---|
| No project exists | Suggest starting with `requirements` |
| Multiple projects | List all, ask user to choose |
| All phases done | Congratulate, suggest `report` or `impact` for changes |
| Automation FAIL | Suggest `flaky-analysis` or re-run `execution` |
| Requirements changed after test-cases | Suggest `impact` analysis |
