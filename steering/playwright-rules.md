# Quy Tắc Dành Riêng Cho Playwright

> Áp dụng khi thiết lập và chạy automation với Playwright (TypeScript, Java, hoặc Python).

## 1. Thiết Lập Browser (BẮT BUỘC)

- **Viewport debug:** 1920×1080
- **Resize bắt buộc:** Khi dùng Playwright MCP, LUÔN gọi `browser_resize(1920, 1080)` ngay sau `browser_navigate`
- **Headed mode:** Bắt buộc khi debug
- **Headless mode:** Chỉ khi test đã PASS 100% hoặc trong CI/CD

Thứ tự bắt buộc:
```
1. browser_navigate(url)
2. browser_resize(width=1920, height=1080)
3. browser_snapshot() hoặc browser_take_screenshot()
```

## 2. Workflow Phát Triển

- Ưu tiên **Playwright MCP** để mở browser và tương tác
- **Inspect DOM thực tế** — verify selector trực tiếp từ browser
- **TUYỆT ĐỐI KHÔNG:** Suy đoán locator, copy locator mù quáng từ code cũ

## 3. Thứ Tự Ưu Tiên Locator

1. `getByRole()` — semantic elements (button, link, heading...)
2. `getByLabel()` — form fields có label
3. `getByPlaceholder()` — inputs có placeholder
4. `getByText()` — text content
5. `getByTestId()` — element có `data-testid`
6. `locator("css")` — Fallback

```typescript
// Đúng
page.getByRole('button', { name: 'Đăng nhập' })
page.getByLabel('Email')

// Sai
page.locator('//button[@class="btn-login"]')
page.locator('.form-input:nth-child(2)')
```

## 4. Chiến Lược Chờ Đợi (Wait Strategy)

**NGHIÊM CẤM:**
- `page.waitForTimeout()` — hard sleep
- `await new Promise(r => setTimeout(r, N))` — tự tạo delay

**SỬ DỤNG:**
- Auto-waiting mặc định của Playwright
- Web-First Assertions:
  ```typescript
  await expect(locator).toBeVisible();
  await expect(locator).toBeEnabled();
  await expect(locator).toHaveText('Thành công');
  await expect(page).toHaveURL(/dashboard/);
  ```

## 5. Cấu Trúc Test

```typescript
test.describe('Tên Module', () => {
  test.beforeEach(async ({ page }) => {
    // Setup: navigate, login...
  });

  test('mô tả hành vi cần test', async ({ page }) => {
    // Arrange → Act → Assert
  });
});
```

- Mỗi test block phải có **assertion rõ ràng**
- Sử dụng `test.describe` để nhóm test theo module
- Sử dụng `beforeEach` / `afterEach` để setup / teardown
