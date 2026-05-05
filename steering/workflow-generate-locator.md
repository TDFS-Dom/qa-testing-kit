# Workflow: Sinh Locator Ổn Định

> Sinh locator stable và dễ bảo trì cho UI automation. Hỗ trợ Playwright, Selenium, Appium.

## Quy trình

1. **Inspect DOM** — KHÔNG BAO GIỜ đoán locator
2. **Xác định stable attributes** — role, label, testid, id, name
3. **Sinh primary locator** — theo priority order của framework
4. **Sinh fallback locator** — alternative nếu primary breaks
5. **Validate uniqueness** — match đúng 1 element
6. **Verify stability** — stable across reloads

## Priority Order

### Playwright
1. `getByRole()` → 2. `getByLabel()` → 3. `getByPlaceholder()` → 4. `getByText()` → 5. `getByTestId()` → 6. CSS → 7. XPath

### Selenium
1. `id` → 2. `data-testid` → 3. `name` → 4. `cssSelector` → 5. `xpath`

### Appium
1. `accessibility id` → 2. `resource-id` → 3. `id` → 4. `iOS predicate` → 5. `class chain` → 6. `xpath`

## Validation Rules

Locator được chấp nhận khi:
- [ ] Match đúng 1 element (unique)
- [ ] Element visible và interactable
- [ ] Stable across page reloads
- [ ] Survives cosmetic DOM changes
- [ ] KHÔNG dùng dynamic class hoặc positional xpath

## Output Format

Với mỗi element, cung cấp:
1. **Primary locator** — best, most stable
2. **Fallback locator** — alternative
3. **Reasoning** — tại sao chọn locator này

## Locator Healing (Sửa locator hỏng)

Khi test fail do locator broken:
1. Đọc error log → xác định locator fail
2. Inspect current DOM → tìm element mới
3. Sinh replacement locator theo priority
4. Verify → replace trong Page Object
5. Re-run test → confirm PASS
