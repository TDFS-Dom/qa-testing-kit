# Workflow: Phân Tích & Khắc Phục Flaky Tests

> Xác định root cause và fix các automation test không ổn định.

## Khi nào dùng

- Test passes và fails xen kẽ
- CI/CD pipeline có kết quả không nhất quán
- Test results khác nhau giữa các lần chạy

## Analysis Workflow

1. **Detect** — Xác định test fail, reproduce failure
2. **Inspect** — Đọc error logs, stack traces, screenshots
3. **Classify** — Phân loại root cause
4. **Fix** — Áp dụng fix strategy phù hợp
5. **Verify** — Chạy lại 5+ lần để confirm stability

## Common Causes & Fixes

### 1. Unstable Locator

**Problem:** Dynamic class, positional xpath
```
//div[3]/button
.css-1n2xyz-btn
```

**Fix:** Thay bằng stable locator:
- `id`, `data-testid`, `name`, CSS selector (stable), relative xpath

### 2. Timing Issues

**Problem:** Hard sleep
```java
Thread.sleep(3000);
page.waitForTimeout(2000);
```

**Fix:** Smart waits:
```java
// Selenium
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));

// Playwright
await expect(page.getByRole('button', { name: 'Submit' })).toBeVisible();
```

### 3. Test Data Conflicts

**Problem:** Tests share mutable data → parallel runs conflict

**Fix:** Unique, traceable random data per test:
```
<testName>_<timestamp>@test.com
```

### 4. Environment Dependency

**Problem:** External service down, data not cleaned up

**Fix:** Mock external services, proper setup/teardown

### 5. Mobile/Appium Flaky
**Problem & Fix:**
- Animation/transition chưa xong → chờ element visible/clickable bằng `WebDriverWait`, không sleep; có thể tắt animation trên emulator
- Keyboard che element → `hideKeyboard()` (trên `AndroidDriver`/`IOSDriver`) trước khi tap
- System popup (permission, alert) → `setAutoGrantPermissions(true)` (Android), `setAutoAcceptAlerts(true)` (iOS)
- Scroll chưa xong/chưa tới element → dùng W3C gesture hoặc `UiScrollable`, rồi chờ element hiển thị
- Session/app state không reset → mỗi test tạo session mới, `quit()` ở teardown, dùng `noReset`/`fullReset` hợp lý
Chi tiết: `appium-rules.md`.

## Stability Checklist

Sau khi fix, verify:
- [ ] Locator unique và stable across reloads
- [ ] No hard sleep hoặc fixed delays
- [ ] Test data unique và deterministic
- [ ] Test independent (no dependency on other tests)
- [ ] Test passes 5+ consecutive runs
