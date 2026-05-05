# Workflow: Sinh Requirements từ Website/Module

> Phân tích UI/DOM của trang web và sinh tài liệu Yêu cầu (Requirements) chi tiết.

## Các bước thực hiện

### 1. Tiếp nhận thông tin

- Lấy URL trang web, tên module, hoặc mô tả/hình ảnh từ user
- Hỏi thông tin đăng nhập hoặc trạng thái đặc biệt nếu cần

### 2. Khảo sát hệ thống (Recon)

- Dùng browser tools/MCP để truy cập module
- Inspect HTML, DOM, forms, buttons, validation messages
- **Không tự đoán** trường thông tin nếu không nhìn thấy trên UI thực tế

### 3. Phân tích chức năng

- Phân tích User Flows
- Ghi nhận trường dữ liệu (TextBox, Dropdown, Checkbox...)
- Ghi nhận Business Rules: mandatory fields, format, giới hạn ký tự

### 4. Biên soạn tài liệu Requirements

Cấu trúc output:

#### 4.1. Tổng quan (Overview)
Mục đích của module/trang.

#### 4.2. Yêu cầu chức năng (Functional Requirements)
User Stories / Use Cases:
- Tên tính năng
- Mô tả: "Là một người dùng, tôi muốn... để có thể..."
- Acceptance Criteria

#### 4.3. Đặc tả Trường Dữ liệu (Field Specifications)
Bảng Markdown:
| Tên Trường | Loại UI | Validation Rules | Ghi chú |
|---|---|---|---|

#### 4.4. Luồng xử lý & Báo lỗi (Business Rules & Validations)
Validation Messages khi nhập sai dữ liệu.

#### 4.5. Câu hỏi/Làm rõ với PO-User
Liệt kê các điểm chưa rõ từ UI.

### 5. Delivery

- Format Markdown, Tiếng Việt
- Lưu file: `practices/requirements/{module}/requirements_{feature}.md`
