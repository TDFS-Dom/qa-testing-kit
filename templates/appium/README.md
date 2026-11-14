# Appium Java Template

Template Appium (Java 17, Maven, TestNG, java-client 9.5.0, Selenium 4.34.0) cho mobile automation Android/iOS.

> **Trạng thái:** `SettingsSmokeTest` (10 test, app Settings có sẵn) đã chạy thật trên emulator AVD **Pixel_6, Android 17 (system image android-37.2)** với Appium server **3.9.0** + uiautomator2 8.7.3: **10/10 pass khi server khởi động với `--allow-insecure=uiautomator2:adb_shell`** (2 lần liên tiếp). Server không có cờ này: 9/10, `androidSpecificApis` fail với `Potentially insecure feature 'adb_shell' has not been enabled`. **Chưa chạy:** iOS/XCUITest, device Android thật, Appium 2.x. `LoginTest` là SAMPLE, chưa chạy với app đăng nhập thật; locator trong `LoginScreen` là placeholder.

## Yêu cầu

- JDK 17, Maven 3.9+
- Node.js (để cài Appium)
- Appium (đã test với 3.9.0): `npm i -g appium`
- Driver: `appium driver install uiautomator2` (Android), `appium driver install xcuitest` (iOS, chỉ macOS + Xcode)
- Kiểm tra môi trường: `npm i -g appium-doctor` rồi `appium-doctor --android` hoặc `appium-doctor --ios`
- Android: đặt `ANDROID_HOME` (vd. `~/Library/Android/sdk`) trước khi chạy Appium; cần Android SDK + emulator (hoặc device thật bật USB debugging). iOS: Xcode + simulator (hoặc device thật có signing)

## Cấu hình

Giá trị placeholder dạng `<...>` được bỏ qua (không gửi lên server). Sửa placeholder trong `src/main/resources/android.properties` hoặc `ios.properties`, hoặc override bằng `-D<key>=<value>` (vd. `-Dapp.path=/path/app.apk`). Credential không lưu trong file, đọc từ biến môi trường:

```bash
export QA_USERNAME=<username>
export QA_PASSWORD=<password>
```

## Chạy

```bash
appium                       # terminal 1: server tại http://127.0.0.1:4723 (không có /wd/hub)
mvn test -Dplatform=android  # terminal 2 (hoặc -Dplatform=ios), chạy testng.xml (LoginTest)
```
Smoke suite Android (app Settings có sẵn, không cần APK/credential). Thay `emulator-5554` bằng udid từ `adb devices`:
```bash
export ANDROID_HOME=$HOME/Library/Android/sdk
appium --allow-insecure=uiautomator2:adb_shell   # cần cho 10/10; thiếu cờ thì androidSpecificApis (mobile: shell) fail, còn 9/10
mvn -q test -Dsurefire.suiteXmlFiles=testng-smoke.xml -Dplatform=android -Dudid=emulator-5554 \
  -Dapp.package=com.android.settings -Dapp.activity=com.android.settings.Settings
# Server cổng khác: thêm -Dappium.server=http://127.0.0.1:<port>
```
Ví dụ app thật (project độc lập): `qa-output/mieo/04_automation/mieo-appium`.

Dùng một emulator/device riêng cho mỗi session Appium. UiAutomator2 server trên device dùng cổng cố định 6790, nên hai session Appium (hai server hoặc hai bộ test) trên cùng một device sẽ xung đột, lỗi `socket hang up` hoặc instrumentation bị chết.

Emulator vừa boot hoặc host đang bận có thể làm tạo session chậm (đã gặp 68s). `DriverFactory` đặt `uiautomator2ServerLaunchTimeout` = 90s và `adbExecTimeout` = 60s (mặc định của driver là 30s/20s, đã gây lỗi `instrumentation process cannot be initialized within 30000ms` và `pm clear ... timed out after 20000ms`). Đổi bằng `-Duia2.launch.timeout.seconds=` / `-Dadb.exec.timeout.seconds=`.

Chỉ kiểm tra compile (không cần device): `mvn -q -DskipTests test-compile`

## Cấu trúc

```
src/main/java/com/qakit/appium/
  config/AppConfig.java        # đọc properties + system property + secret từ env
  utils/DriverFactory.java     # ThreadLocal<AppiumDriver>, UiAutomator2Options / XCUITestOptions
  utils/GestureHelper.java     # W3C gestures (tap, swipe, long press, double tap, UiScrollable)
  pages/BasePage.java          # lớp cơ sở: smart waits, tap/type/getText, hideKeyboard
  screens/LoginScreen.java     # Screen Object dùng chung Android + iOS
src/main/resources/            # android.properties, ios.properties (placeholder)
src/test/java/.../tests/       # BaseTest (teardown + artifact khi lỗi), LoginTest (sample), SettingsSmokeTest
testng.xml                     # suite LoginTest (sample)
testng-smoke.xml               # suite SettingsSmokeTest (Android, app Settings)
```

`BasePage` là lớp cơ sở chung; màn hình cụ thể là Screen Object, đặt tên có hậu tố `Screen`.

## Artifact và báo cáo

- Khi test lỗi: screenshot + page source lưu ở `target/artifacts/<test>_<timestamp>/`.
- Kết quả: `target/surefire-reports/` (có file `TEST-*.xml` dạng JUnit) dùng để import lên Xray với `format: "junit"`.

## Nguồn tham khảo

Cấu trúc ý tưởng tham khảo skill "appium-mobile" của thetestingacademy (giấy phép MIT): https://github.com/PramodDutta/qaskills . Code được viết lại và đã compile và chạy smoke test Android với java-client 9.5.0.
