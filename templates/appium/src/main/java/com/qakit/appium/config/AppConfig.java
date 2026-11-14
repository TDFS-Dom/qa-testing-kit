package com.qakit.appium.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Đọc cấu hình từ {@code <platform>.properties} (classpath), cho phép override bằng system property
 * (vd. {@code -Dapp.path=/path/app.apk}). Credential test lấy từ env var hoặc system property,
 * không bao giờ hardcode.
 */
public final class AppConfig {

    private static final Properties PROPS = new Properties();

    static {
        String file = platform() + ".properties";
        try (InputStream in = AppConfig.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new IllegalStateException("Không tìm thấy file cấu hình trên classpath: " + file);
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Không đọc được file cấu hình: " + file, e);
        }
    }

    private AppConfig() {
    }

    /** "android" hoặc "ios" (mặc định android). Chọn bằng -Dplatform=ios. */
    public static String platform() {
        String value = System.getProperty("platform", "android").trim().toLowerCase();
        if (!value.equals("android") && !value.equals("ios")) {
            throw new IllegalArgumentException("platform phải là android hoặc ios, nhận được: " + value);
        }
        return value;
    }

    public static String get(String key) {
        String value = System.getProperty(key, PROPS.getProperty(key));
        if (value == null) {
            return "";
        }
        value = value.trim();
        // Placeholder dạng <TEN_GIA_TRI> coi như chưa cấu hình (tránh gửi "<PATH_TO_APK>" lên server)
        return value.startsWith("<") && value.endsWith(">") ? "" : value;
    }

    public static String serverUrl() {
        return get("appium.server");
    }

    public static int waitTimeoutSeconds() {
        return seconds("wait.timeout.seconds", 15);
    }

    /** Đọc số giây từ cấu hình, dùng giá trị mặc định nếu chưa set. */
    public static int seconds(String key, int defaultValue) {
        String value = get(key);
        return value.isEmpty() ? defaultValue : Integer.parseInt(value);
    }

    /** Đọc credential từ system property rồi tới env var; lỗi rõ ràng nếu thiếu. */
    public static String requiredSecret(String name) {
        String value = System.getProperty(name, System.getenv(name));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Thiếu biến cấu hình bắt buộc: " + name
                    + " (đặt bằng env var hoặc -D" + name + "=...)");
        }
        return value;
    }
}
