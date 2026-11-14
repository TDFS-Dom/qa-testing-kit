package com.qakit.appium.utils;

import com.qakit.appium.config.AppConfig;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

/**
 * Tạo và quản lý driver theo thread. Mỗi thread giữ đúng 1 session/1 device.
 *
 * Tham khảo cấu trúc từ skill "appium-mobile" của thetestingacademy (MIT):
 * https://github.com/PramodDutta/qaskills . Code đã được viết lại cho java-client 9.x.
 *
 * Lưu ý: Appium 2 không dùng đường dẫn /wd/hub. Emulator/simulator và device thật khác nhau ở udid:
 * chỉ set udid khi cấu hình có giá trị (emulator: "emulator-5554" từ adb devices; device thật: udid thật).
 */
public final class DriverFactory {

    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static AppiumDriver createDriver(String platform) {
        URL server = serverUrl();
        AppiumDriver driver = switch (platform) {
            case "android" -> new AndroidDriver(server, androidOptions());
            case "ios" -> new IOSDriver(server, iosOptions());
            default -> throw new IllegalArgumentException("Platform không hỗ trợ: " + platform);
        };
        DRIVER.set(driver);
        return driver;
    }

    public static AppiumDriver getDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("Driver chưa được khởi tạo cho thread hiện tại");
        }
        return driver;
    }

    public static void quitDriver() {
        AppiumDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }

    private static UiAutomator2Options androidOptions() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setAutoGrantPermissions(true)
                .setNewCommandTimeout(Duration.ofSeconds(300))
                // Mặc định của driver (30s launch, 20s adb) quá ngắn khi emulator/host bận:
                // đã gặp "instrumentation process cannot be initialized within 30000ms" và
                // "pm clear ... timed out after 20000ms" trên emulator vừa boot.
                .setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(AppConfig.seconds("uia2.launch.timeout.seconds", 90)))
                .setAdbExecTimeout(Duration.ofSeconds(AppConfig.seconds("adb.exec.timeout.seconds", 60)));
        // Chỉ set capability khi có giá trị: có thể chạy bằng app.path (APK) hoặc app.package + app.activity
        String deviceName = AppConfig.get("device.name");
        if (!deviceName.isEmpty()) {
            options.setDeviceName(deviceName);
        }
        String platformVersion = AppConfig.get("platform.version");
        if (!platformVersion.isEmpty()) {
            options.setPlatformVersion(platformVersion);
        }
        String appPath = AppConfig.get("app.path");
        if (!appPath.isEmpty()) {
            options.setApp(appPath);
        }
        String appPackage = AppConfig.get("app.package");
        if (!appPackage.isEmpty()) {
            options.setAppPackage(appPackage);
        }
        String appActivity = AppConfig.get("app.activity");
        if (!appActivity.isEmpty()) {
            options.setAppActivity(appActivity);
        }
        String udid = AppConfig.get("udid");
        if (!udid.isEmpty()) {
            options.setUdid(udid);
        }
        // Mặc định giữ hành vi cũ (noReset=false). -Dno.reset=true để giữ dữ liệu app, không pm clear.
        if (Boolean.parseBoolean(AppConfig.get("no.reset"))) {
            options.setNoReset(true);
        }
        return options;
    }

    private static XCUITestOptions iosOptions() {
        XCUITestOptions options = new XCUITestOptions()
                .setDeviceName(AppConfig.get("device.name"))
                .setPlatformVersion(AppConfig.get("platform.version"))
                .setApp(AppConfig.get("app.path"))
                .setBundleId(AppConfig.get("bundle.id"))
                .setAutoAcceptAlerts(true)
                .setWdaLaunchTimeout(Duration.ofSeconds(120))
                .setNewCommandTimeout(Duration.ofSeconds(300));
        String udid = AppConfig.get("udid");
        if (!udid.isEmpty()) {
            options.setUdid(udid);
        }
        return options;
    }

    private static URL serverUrl() {
        try {
            return URI.create(AppConfig.serverUrl()).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("appium.server không hợp lệ: " + AppConfig.serverUrl(), e);
        }
    }
}
