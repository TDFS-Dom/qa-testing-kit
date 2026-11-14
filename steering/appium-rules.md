# Quy Tắc Dành Riêng Cho Appium (Mobile Automation)
> Mọi code Java trong file này đã compile với java-client 9.5.0 (`.agents/tasks/appium-verification.md`). Phần Android (mục 2, 6, 7) đã chạy thật trên emulator riêng `emulator-5556` (AVD Pixel_6, Android 17, API 37.2) với Appium server 3.9.0 + uiautomator2 8.7.3 do chính người verify khởi động: smoke suite 10/10 pass 2 lần liên tiếp (server có `--allow-insecure=uiautomator2:adb_shell`), 9/10 khi thiếu cờ. Bảng PASS/FAIL/NOT RUN ở `.agents/tasks/appium-device-run-report.md`. Chưa chạy: iOS/XCUITest (driver chưa cài), device Android thật, các mục đánh dấu "chưa verify" bên dưới.

## 1. Scope
- **Chỉ Java**, java-client **9.x** (+ Selenium 4.34.0, TestNG). Đã verify với Appium server **3.9.0** + uiautomator2 **8.7.3** (Android emulator). Appium 2.x chưa được test. Không dùng TS/Python với Appium.
- Code mẫu đầy đủ: `templates/appium/` (đọc khi scaffold, không load mặc định). Nguồn tham khảo ý tưởng: skill appium-mobile của thetestingacademy (MIT).

## 2. Khởi Tạo Driver
- Dùng Options class, không dùng `DesiredCapabilities`. Setter của Options tự thêm prefix `appium:`.
- Dùng driver typed `AndroidDriver` / `IOSDriver`. Server URL: `http://127.0.0.1:4723` (**không** `/wd/hub`). Chạy app có sẵn (vd. Settings) chỉ cần `setAppPackage` + `setAppActivity`, không set `app`.
```java
UiAutomator2Options options = new UiAutomator2Options()
    .setDeviceName("<DEVICE>").setApp("<PATH_TO_APK>").setAutoGrantPermissions(true);
AppiumDriver driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);

XCUITestOptions ios = new XCUITestOptions()
    .setDeviceName("<DEVICE>").setBundleId("<BUNDLE_ID>").setAutoAcceptAlerts(true);
```
| | Android | iOS |
|---|---|---|
| Options / driver | `UiAutomator2Options` / `AndroidDriver` | `XCUITestOptions` / `IOSDriver` |
| Permission | `setAutoGrantPermissions(true)` | `setAutoAcceptAlerts(true)` (alert) |
| App | `setApp`, `setAppPackage`, `setAppActivity` | `setApp`, `setBundleId` |
- `setUdid(...)` chỉ khi cần chọn device cụ thể (`adb devices` cho Android, udid thật cho iOS device). Không hardcode, lấy từ config.

## 3. API Cấm / Đã Bỏ
| Cấm | Thay bằng |
|---|---|
| `TouchAction`, `MultiTouchAction` | W3C `PointerInput` + `Sequence` + `driver.perform(...)` |
| `MobileElement` | `WebElement` |
| `MobileBy` | `AppiumBy` |
| `DesiredCapabilities` legacy | `UiAutomator2Options` / `XCUITestOptions` |
| URL có `/wd/hub` | `http://127.0.0.1:4723` |
| `launchApp()`, `closeApp()`, `resetApp()` | `activateApp(id)`, `terminateApp(id)`, `noReset`/`fullReset` option |
| `Thread.sleep()` | `WebDriverWait` (mục 5) |
| `catch (Exception ignored)` | Bắt exception cụ thể (`NoSuchElementException`, `TimeoutException`) |

## 4. Locator Priority
Thứ tự (nguồn chính, `locator-strategy.md` và `workflow-generate-locator.md` chỉ trỏ về đây):
1. `AppiumBy.accessibilityId` (cross-platform, ổn định nhất)
2. `AppiumBy.id` (Android `resource-id`, iOS `name`)
3. iOS: `AppiumBy.iOSClassChain` / `AppiumBy.iOSNsPredicateString`
4. Android: `AppiumBy.androidUIAutomator`
5. `AppiumBy.className`
6. `By.xpath`: lựa chọn cuối, tương đối, không tuyệt đối theo vị trí
- Không dùng XPath theo text để xử lý permission dialog. Dùng `setAutoGrantPermissions` / `setAutoAcceptAlerts`.
- Nên khai báo bằng `@AndroidFindBy` + `@iOSXCUITFindBy` với `AppiumFieldDecorator`.

## 5. Smart Waits
```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("welcome_text")));
wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
```
- Không `Thread.sleep`, không hardcode thời gian chờ animation. Không trộn implicit wait với explicit wait.
- Chỉ tương tác element đã visible/enabled; element ngoài màn hình phải scroll trước.

## 6. Gestures (W3C)
```java
PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
Sequence swipe = new Sequence(finger, 0)
    .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x1, y1))
    .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
    .addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), x2, y2))
    .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
driver.perform(List.of(swipe));
```
- Tính toạ độ theo `driver.manage().window().getSize()`, không hardcode pixel.
- Android scroll tới text: `AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"Submit\"))")`.
- Tap, swipe 4 hướng, long press, double tap: xem `utils/GestureHelper.java` trong template.

## 7. App Lifecycle & Đặc Thù Mobile
Trên `AndroidDriver` / `IOSDriver` (không có trên `AppiumDriver` base):
```java
driver.activateApp("<APP_ID>");                 // đưa app ra foreground
driver.terminateApp("<APP_ID>");
driver.runAppInBackground(Duration.ofSeconds(3)); // background rồi tự quay lại
driver.rotate(ScreenOrientation.LANDSCAPE);
driver.executeScript("mobile: deepLink", Map.of("url", "myapp://home", "package", "<PACKAGE>")); // iOS: "bundleId" (chưa verify); Android: url phải được package đó resolve, nếu không lỗi "unable to resolve Intent"
if (driver.isKeyboardShown()) { driver.hideKeyboard(); }
```
- Android: `pressKey(new KeyEvent(AndroidKey.BACK))`, `isAppInstalled`, `queryAppState` (đã chạy thật). `installApp` chưa chạy.
- `rotate`, `getOrientation`, `runAppInBackground`, `isKeyboardShown`, `hideKeyboard` nằm trên `AndroidDriver`/`IOSDriver`, không có trên `AppiumDriver`: ép kiểu trước khi gọi.
- `mobile: shell` chỉ chạy khi server khởi động với `--allow-insecure=uiautomator2:adb_shell`. Thiếu cờ, lệnh ném lỗi `Potentially insecure feature 'adb_shell' has not been enabled` (đã chạy thật cả hai trường hợp).
- Network on/off: dùng `mobile: shell` (Android) hoặc cấu hình emulator/simulator; chưa verify runtime.
- Test cả portrait và landscape, background rồi quay lại app.

## 8. Screen Object
- Mobile dùng Screen Object, hậu tố `Screen`: `LoginScreen.java`, `HomeScreen.java`. `BasePage` chỉ là lớp cơ sở chung.
- Cross-platform: **một class** cho cả 2 platform, mỗi field gắn `@AndroidFindBy` + `@iOSXCUITFindBy`. Chỉ tách class khi UI khác hẳn.
- Method theo hành vi (`login(user, pass)`), assertion nằm trong test.

## 9. Inspect UI Thật Trước
Không đoán locator. Mở app trên device/emulator, inspect bằng Appium Inspector hoặc `driver.getPageSource()`, rồi mới viết locator. Nếu app thiếu accessibility id, đề xuất dev bổ sung.

## 10. Parallel Execution
- `ThreadLocal<AppiumDriver>` trong `DriverFactory`; teardown luôn `quit()` + `remove()`.
- Mỗi thread dùng đúng 1 device (udid và port riêng khi chạy nhiều device); `parallel="tests"` trong `testng.xml`.

## 11. Failure Artifacts
Khi test fail, lưu screenshot (`TakesScreenshot`) và `driver.getPageSource()` vào `target/artifacts/<test>_<timestamp>/` trước khi `quit()`. Xem `tests/BaseTest.java`.

## 12. Reporting Lên Xray
Surefire sinh TestNG/JUnit XML ở `target/surefire-reports/TEST-*.xml`. Import bằng `xray_import_results` với `"format": "junit"` (xem `workflow-jira-integration.md`).
