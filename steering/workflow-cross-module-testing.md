# Workflow: Phân Tích Cross-Module & Ma Trận Kết Hợp

> Dành cho tính năng phức tạp đi qua nhiều modules nối tiếp. Sinh Data Flow Map + Ma trận kết hợp đa chiều (Pairwise / Business-critical / Full Cartesian).

## Khi nào dùng

- Tính năng đi qua chuỗi N modules (VD: Đối tác → Thanh toán → Thuế → Biên bản)
- Data module sau phụ thuộc output module trước
- Cần test nhiều tổ hợp điều kiện kết hợp

## Các bước thực hiện

### 1. Xác định Module Chain

- Liệt kê tất cả modules trong flow
- Xác định thứ tự thực hiện (sequential / parallel)
- Map dependencies: module nào cần output từ module nào

### 2. Sinh Data Flow Map

```
Module 1 (Input: A, B) → Output: {id_1, code_1}
    ↓
Module 2 (Input: id_1, C, D) → Output: {id_2}
    ↓
Module 3 (Input: id_1, id_2, E) → Output: Final Result
```

### 3. Xác định Dimensions (Chiều kết hợp)

- Mỗi module có các "chiều" ảnh hưởng đến kết quả
- Ví dụ: Loại đối tác × Phương thức thanh toán × Loại thuế × Template biên bản

### 4. Sinh Ma Trận Kết Hợp

| Strategy | Khi nào dùng | Số combos |
|---|---|---|
| **Pairwise** | Nhiều dimensions, cần giảm số lượng | ~N×logN |
| **Business-critical** | Chỉ test các combo quan trọng nhất | Manual selection |
| **Full Cartesian** | Ít dimensions, cần bao phủ 100% | N₁×N₂×...×Nₖ |

### 5. Sinh Test Data cho Ma Trận

Với mỗi combo trong ma trận:
- Dimension values: lấy CHÍNH XÁC từ ma trận (KHÔNG random)
- Supporting fields: random + unique + traceable
- Reference fields: copy từ output module trước
- Expected output: tính theo formula/business rules

### 6. Execution Strategy

**Mode GENERATE (offline):**
- Sinh bộ data hoàn chỉnh dạng JSON/Excel
- QA team tự nhập data vào hệ thống

**Mode PIPELINE (browser automation):**
- Chạy thật trên browser qua N modules
- Mỗi combo = 1 pipeline run
- Thu thập output mỗi module → feed vào module tiếp theo

## Output

- Data Flow Map (diagram)
- Ma trận kết hợp (Markdown table)
- Test data cho từng combo
- Expected results cho từng combo
- Execution plan (thứ tự chạy)
