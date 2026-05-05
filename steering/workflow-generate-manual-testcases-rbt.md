# Workflow: Sinh Manual Test Cases theo AI-RBT Framework (FULL RBT — 6 Bước)

> Quy trình bài bản, tuần tự cho module phức tạp. Bao gồm phân tích Ambiguity, phân rã hệ thống, Traceability Matrix, đánh giá Risk Level, và sinh test cases chi tiết.

## ⚠️ Nguyên tắc thực thi

- **BẮT BUỘC chạy tuần tự** từng bước, KHÔNG gộp nhiều bước
- **PHẢI dừng lại** chờ user phản hồi tại Bước 2 (Q&A) và Bước 4 (Review Scenarios)
- Tất cả output bằng **Tiếng Việt**

## Bước 1: Khởi tạo ngữ cảnh (Context & Role-play)

1. Yêu cầu user cung cấp: tên dự án, mô tả hệ thống, mục tiêu MVP, tài liệu yêu cầu
2. Đọc kỹ tài liệu, xác nhận hiểu bối cảnh
3. Tóm tắt scope kiểm thử
4. **Chờ user xác nhận** → sang Bước 2

## Bước 2: Phân tích yêu cầu (Analysis & QnA)

1. Xác định: Happy Path, Alternate Paths, Exception Paths
2. Phát hiện Ambiguities (thiếu sót, mâu thuẫn, chưa rõ ràng)
3. Đặt câu hỏi Q&A có đánh số (Q1, Q2...) kèm ngữ cảnh + assumption
4. **DỪNG LẠI — Chờ user trả lời** → sang Bước 3

> ⚠️ Đây là điểm nghẽn quan trọng nhất. Nếu bỏ qua và tự đoán logic, test cases sẽ sai.

## Bước 3: Phân rã hệ thống (Decomposition)

1. Chia tính năng thành Modules / Sub-modules (theo UI hoặc theo luồng)
2. Mô tả chức năng từng Module + Dependencies

## Bước 4: Đảm bảo độ bao phủ (Traceability)

1. Map Module → mã Yêu cầu (REQ-01, REQ-02...)
2. Cross-check thiếu sót (Gap Analysis)
3. Liệt kê High-Level Test Scenarios (Security, UI Validation, Business Logic, Data Integrity, Error Handling)
4. **Chờ user review** scenarios → sang Bước 5

## Bước 5: Sinh Test Case chi tiết (RBT & TC Generation)

1. Đánh giá Risk Level (High/Medium/Low) cho mỗi Module
2. Sinh test cases đầy đủ: Title, Pre-condition, Steps, Expected, Test Data, Priority
3. Áp dụng kỹ thuật: EP, BVA, Decision Table, State Transition
4. Test Data phải **cụ thể** (không placeholder)
5. Nếu quá nhiều → sinh từng Module, hỏi user để tiếp tục

## Bước 6: Chuẩn hóa Format (Template Mapping)

1. Đóng gói toàn bộ vào bảng Markdown:
   ```
   | TC ID | Module | Risk Level | Test Title | Pre-Condition | Test Steps | Expected Result | Priority | Test Data |
   ```
2. TC ID format: `[DỰ_ÁN]_[MODULE]_TC_[SỐ]`
3. KHÔNG được bỏ sót test case nào
4. Nếu quá dài → chia Part 1, Part 2...

## Output

- Bảng Test Cases Markdown hoàn chỉnh (Jira/Excel ready)
- Traceability Matrix
- Danh sách Ambiguities đã giải quyết

## Anti-Patterns (NGHIÊM CẤM)

- ❌ Gộp nhiều bước chạy 1 lần
- ❌ Tự đoán business logic khi chưa hỏi user
- ❌ Bỏ qua bước phân tích Ambiguity
- ❌ Sinh test data chung chung
- ❌ Rút gọn hoặc bỏ sót test case khi mapping sang bảng
- ❌ Test Steps mơ hồ, không ghi rõ dữ liệu nhập
