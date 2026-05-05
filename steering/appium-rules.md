# Quy Tắc Dành Riêng Cho Appium (Mobile Automation)

> Áp dụng khi tự động hóa ứng dụng mobile với Java và Appium.

## 1. Thứ Tự Ưu Tiên Locator

1. `accessibility id` — Cross-platform, ổn định nhất
2. `resource-id` (Android)
3. `id`
4. `iOS predicate string` (iOS)
5. `iOS class chain` (iOS)
6. `xpath` — Lựa chọn cuối cùng (chậm nhất)

```java
// Đúng
driver.findElement(AppiumBy.accessibilityId("login_button"));
driver.findElement(AppiumBy.id("com.application.xyz:id/login_button"));
driver.findElement(AppiumBy.iOSNsPredicateString("label == 'Login'"));

// NGHIÊM CẤM: XPath tuyệt đối
driver.findElement(By.xpath("//android.widget.FrameLayout[1]/android.widget.LinearLayout[2]/android.widget.Button[1]"));
```

## 2. NGHIÊM CẤM

- XPath tuyệt đối dựa trên vị trí
- Truy vấn element nằm ngoài màn hình mà không scroll trước
- Tương tác với element bị disabled mà không kiểm tra trạng thái
- Hardcode thời gian chờ animation

## 3. Chiến Lược Chờ Đợi

**NGHIÊM CẤM:** `Thread.sleep()`

**SỬ DỤNG:** Explicit Waits:
```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("welcome_text")));
```

Scroll đến element (Android):
```java
driver.findElement(AppiumBy.androidUIAutomator(
    "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"Submit\"))"
));
```

## 4. Cấu Trúc Test

- Mobile dùng **Screen Objects** (hậu tố `Screen`): `LoginScreen.java`, `HomeScreen.java`
- TestNG structure tương tự Selenium

## 5. Đặc Thù Mobile

- **Xoay màn hình:** Test cả portrait và landscape
- **Background/Foreground:** Test app khi chuyển ra nền rồi quay lại
- **Permission Dialog:** Xử lý dialog xin quyền:
  ```java
  capabilities.setCapability("autoGrantPermissions", true);
  ```
