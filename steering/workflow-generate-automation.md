# Workflow: Sinh Automation Scripts từ Manual Test Cases

> Đọc file manual test cases → inspect UI → thu thập locators → sinh automation scripts (POM + Test) → chạy test → tự sửa lỗi cho đến khi PASS.

## ⚠️ Nguyên tắc

- **TUYỆT ĐỐI KHÔNG ĐOÁN locator** — phải inspect DOM thực tế
- **Desktop viewport 1920×1080**
- **Rule E3:** Khi test FAIL → tự đọc log → phân tích → sửa → chạy lại. CẤM hỏi user trong quá trình fix (trừ business rule mâu thuẫn hoặc hết 5 vòng auto-heal)

## Input cần thu thập

| Input | Độ ưu tiên |
|---|---|
| File test cases (MD/Excel/JSON) | ⭐ Bắt buộc |
| URL ứng dụng | ⭐ Bắt buộc |
| Credentials (nếu cần login) | Tùy chọn |
| Tech stack | Tùy chọn (mặc định: Playwright TS) |

## Các bước thực hiện

### Bước 1: Phân tích & Lên Kế Hoạch

1. Đọc file test cases, parse: TC ID, Title, Steps, Expected Results, Test Data, Priority
2. Xác định pages/screens mà TC đi qua
3. Xác định tech stack (Playwright TS / Selenium Java / Appium Java)
4. Tạo checklist `task.md` theo dõi tiến độ

### Bước 2: Khảo sát UI (MCP Recon)

1. Mở browser: `navigate → resize(1920×1080) → wait → snapshot`
2. Với mỗi page: thu thập locator tốt nhất cho mỗi element
3. Verify locator bằng cách thử tương tác
4. Ghi nhận bảng Locator Collection (Primary + Fallback)

### Bước 3: Thiết kế POM

1. Xác định danh sách Page classes
2. Sinh Page Object classes: Locators + Action methods + Verification methods
3. Method name mô tả hành vi: `login()`, `fillForm()`, không phải `clickButton()`

### Bước 4: Chuẩn bị Test Data

1. Data unique per run → random + traceable (`auto_login_1712049200@test.com`)
2. Data cố định → đọc từ env/config
3. Sensitive data → env variables, KHÔNG hardcode

### Bước 5: Sinh Test Classes

- Cấu trúc: Arrange → Act → Assert
- Mỗi TC có ít nhất 1 assertion với message rõ ràng
- Không inline locator, không hard sleep, test independent

### Bước 6: Chạy & Auto-Heal (Rule E3)

```
WHILE test FAIL (tối đa 5 vòng):
  1. Đọc error log → xác định step fail
  2. Phân loại: Element not found / Timeout / Assertion fail / Data conflict
  3. Sửa code
  4. Chạy lại
```

Test phải PASS **2 lần liên tiếp** để confirm stability.

### Bước 7: Cleanup & Delivery

- [ ] Xóa debug logs, commented code
- [ ] Verify POM compliance
- [ ] Báo cáo: X PASS / Y FAIL / Z SKIP
- [ ] Danh sách files đã tạo

## Output

- Page Object classes (locators verified từ DOM)
- Test classes (đã PASS stable)
- Test data utilities
- Bảng Locator Collection
- Báo cáo kết quả
