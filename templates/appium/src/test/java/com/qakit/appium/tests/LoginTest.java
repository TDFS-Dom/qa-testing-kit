package com.qakit.appium.tests;

import com.qakit.appium.config.AppConfig;
import com.qakit.appium.screens.LoginScreen;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SAMPLE: cần Appium server đang chạy + device/emulator/simulator + app thật.
 * Không chạy được trong CI không có device. Locator trong LoginScreen là placeholder.
 * Credential lấy từ env var QA_USERNAME / QA_PASSWORD, không hardcode.
 */
public class LoginTest extends BaseTest {

    @Test(description = "SAMPLE: đăng nhập hợp lệ hiển thị màn hình chính (cần device + app thật)")
    public void testLoginWithValidCredentials() {
        LoginScreen login = new LoginScreen(driver);
        Assert.assertTrue(login.isLoaded(), "Màn hình đăng nhập chưa hiển thị");

        login.login(AppConfig.requiredSecret("QA_USERNAME"), AppConfig.requiredSecret("QA_PASSWORD"));

        // TODO: thay bằng assert màn hình đích của app thật (vd. HomeScreen.isLoaded()).
    }

    @Test(description = "SAMPLE: sai mật khẩu hiển thị thông báo lỗi (cần device + app thật)")
    public void testLoginWithInvalidPasswordShowsError() {
        LoginScreen login = new LoginScreen(driver);
        // Data truy vết theo quy ước <prefix>_<testName>_<timestamp>
        String invalidPassword = "AUTO_testLoginInvalidPassword_" + System.currentTimeMillis();

        login.login(AppConfig.requiredSecret("QA_USERNAME"), invalidPassword);

        Assert.assertFalse(login.getErrorMessage().isEmpty(), "Phải có thông báo lỗi");
    }
}
