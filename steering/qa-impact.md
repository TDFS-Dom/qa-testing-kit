# QA Testing Kit — Impact Analysis

Workflow phân tích ảnh hưởng khi requirements hoặc test artifacts thay đổi.

## Khi nào trigger

- User yêu cầu thay đổi requirements đã lock
- User sửa test cases đã sinh
- User thêm/bớt field, rule, hoặc flow
- Bare correction statement ("thêm validation cho field X")

## Workflow

### 1. Capture Change Request

```
📝 Change Request
Input: {user's change description}
Scope: {which artifact(s) affected}
```

### 2. Trace Impact

Scan downstream artifacts để xác định ảnh hưởng:

```
Requirements changed
  → Test Plan affected? (coverage gaps)
  → Test Cases affected? (new/modified/deleted TCs)
  → Automation affected? (locators, steps, assertions)
  → Test Data affected? (new fields, new boundaries)
```

### 3. Present Impact Matrix

```
🔍 Impact Analysis
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Change: {description}

Affected Artifacts:
| Artifact | Module | Impact | Action Needed |
|---|---|---|---|
| requirements.md | — | Direct | Update section X |
| test-cases.md | auth | Add 2 TCs | Sinh TC mới cho validation |
| login.page.ts | auth | Add locator | Inspect DOM, thêm field |
| login.spec.ts | auth | Add assertion | Thêm test step |

Risk Level: {Low/Medium/High}
Estimated Effort: {number of artifacts to update}
```

### 4. User Approval

```
Bạn muốn:
1. ✅ Approve — Tôi sẽ cập nhật tất cả artifacts bị ảnh hưởng
2. 🔄 Partial — Chỉ cập nhật {specific artifacts}
3. ❌ Cancel — Không thay đổi gì
```

### 5. Execute (after approval)

- Update artifacts theo thứ tự: requirements → test-cases → automation
- Cập nhật `project-memory.md` với correction record
- Re-verify automation nếu code bị thay đổi

## Rules

- **KHÔNG mutate** bất kỳ artifact nào trước khi user approve
- **KHÔNG skip** impact analysis cho bare corrections
- Nếu change nhỏ (typo, wording) → vẫn show impact nhưng note "Low risk, wording only"
- Nếu change lớn (new flow, new module) → suggest re-run affected phases

## Impact Severity

| Severity | Criteria | Action |
|---|---|---|
| Low | Wording, typo, cosmetic | Update in-place, no re-test |
| Medium | New field, new validation rule | Update TC + automation, re-test affected |
| High | New flow, new module, architecture change | Re-run from test-plan phase |
