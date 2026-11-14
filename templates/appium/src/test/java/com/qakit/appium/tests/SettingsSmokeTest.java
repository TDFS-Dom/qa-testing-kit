package com.qakit.appium.tests;

import com.qakit.appium.pages.BasePage;
import com.qakit.appium.utils.GestureHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * Smoke test chạy trên app Settings có sẵn của Android (không cần APK).
 * Chạy bằng: {@code mvn test -Dsurefire.suiteXmlFiles=testng-smoke.xml -Dapp.package=com.android.settings
 * -Dapp.activity=com.android.settings.Settings} (xem README).
 */
public class SettingsSmokeTest extends BaseTest {

    private static final String SETTINGS_PACKAGE = "com.android.settings";
    private static final Path ARTIFACT_DIR = Paths.get("target", "smoke-artifacts");

    /** Dùng BasePage đúng cách: kế thừa để có smart wait, PageFactory init. */
    private static final class SettingsScreen extends BasePage {
        SettingsScreen(io.appium.java_client.AppiumDriver driver) {
            super(driver);
        }

        void waitLoaded() {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.id(SETTINGS_PACKAGE + ":id/settings_homepage_container")));
        }

        boolean isLoaded() {
            return !driver.findElements(AppiumBy.id(SETTINGS_PACKAGE + ":id/settings_homepage_container")).isEmpty();
        }
    }

    private AndroidDriver android() {
        return (AndroidDriver) driver;
    }

    private static final String HOME_CONTAINER = SETTINGS_PACKAGE + ":id/settings_homepage_container";

    @Test(description = "Settings mở được và hiển thị màn hình chính")
    public void settingsLaunchesAndShowsHome() {
        SettingsScreen screen = new SettingsScreen(driver);
        screen.waitLoaded();
        Assert.assertEquals(android().getCurrentPackage(), SETTINGS_PACKAGE);
        Assert.assertTrue(screen.isLoaded());
    }

    @Test(description = "UiScrollable cuộn tới 'System' và W3C swipe qua GestureHelper")
    public void scrollAndSwipe() {
        new SettingsScreen(driver).waitLoaded();
        GestureHelper gestures = new GestureHelper(driver);
        Assert.assertTrue(gestures.scrollToTextAndroid("System").isDisplayed(), "'System' phải hiển thị sau khi scroll");
        // Vuốt lên rồi xuống bằng W3C Actions; màn hình vẫn phải ở Settings.
        gestures.swipe(GestureHelper.Direction.DOWN);
        gestures.swipe(GestureHelper.Direction.UP);
        Assert.assertEquals(android().getCurrentPackage(), SETTINGS_PACKAGE);
    }

    @Test(description = "Screenshot và page source được ghi ra file")
    public void screenshotAndPageSourceArtifacts() throws IOException {
        new SettingsScreen(driver).waitLoaded();
        Files.createDirectories(ARTIFACT_DIR);
        Path png = ARTIFACT_DIR.resolve("settings.png");
        Path xml = ARTIFACT_DIR.resolve("settings-page-source.xml");
        Files.write(png, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        Files.writeString(xml, driver.getPageSource());
        Assert.assertTrue(Files.size(png) > 0, "Screenshot rỗng");
        Assert.assertTrue(Files.size(xml) > 0, "Page source rỗng");
    }

    @Test(description = "Xoay LANDSCAPE rồi về PORTRAIT")
    public void rotateLandscapeAndBack() {
        new SettingsScreen(driver).waitLoaded();
        try {
            android().rotate(ScreenOrientation.LANDSCAPE);
            waitFor(d -> android().getOrientation() == ScreenOrientation.LANDSCAPE);
            Assert.assertEquals(android().getOrientation(), ScreenOrientation.LANDSCAPE);
        } finally {
            android().rotate(ScreenOrientation.PORTRAIT);
        }
        waitFor(d -> android().getOrientation() == ScreenOrientation.PORTRAIT);
        Assert.assertEquals(android().getOrientation(), ScreenOrientation.PORTRAIT);
    }

    @Test(description = "runAppInBackground rồi app quay lại foreground")
    public void backgroundAndReturn() {
        new SettingsScreen(driver).waitLoaded();
        android().runAppInBackground(Duration.ofSeconds(3));
        SettingsScreen screen = new SettingsScreen(driver);
        screen.waitLoaded();
        Assert.assertEquals(android().getCurrentPackage(), SETTINGS_PACKAGE);
    }

    @Test(description = "terminateApp rồi activateApp")
    public void terminateAndActivate() {
        new SettingsScreen(driver).waitLoaded();
        Assert.assertTrue(android().terminateApp(SETTINGS_PACKAGE), "terminateApp phải trả true");
        waitFor(d -> android().queryAppState(SETTINGS_PACKAGE) == io.appium.java_client.appmanagement.ApplicationState.NOT_RUNNING);
        android().activateApp(SETTINGS_PACKAGE);
        new SettingsScreen(driver).waitLoaded();
        Assert.assertEquals(android().queryAppState(SETTINGS_PACKAGE),
                io.appium.java_client.appmanagement.ApplicationState.RUNNING_IN_FOREGROUND);
    }

    @Test(description = "UiScrollable đúng như ví dụ trong steering (scrollable(true) không instance)")
    public void uiScrollableAsInSteering() {
        new SettingsScreen(driver).waitLoaded();
        Assert.assertTrue(driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"System\"))"))
                .isDisplayed());
    }

    @Test(description = "isAppInstalled, queryAppState, mobile: shell, pressKey BACK")
    public void androidSpecificApis() {
        new SettingsScreen(driver).waitLoaded();
        Assert.assertTrue(android().isAppInstalled(SETTINGS_PACKAGE));
        Assert.assertEquals(android().queryAppState(SETTINGS_PACKAGE),
                io.appium.java_client.appmanagement.ApplicationState.RUNNING_IN_FOREGROUND);
        Object out = android().executeScript("mobile: shell", java.util.Map.of("command", "echo", "args", java.util.List.of("hello")));
        Assert.assertTrue(String.valueOf(out).contains("hello"), "mobile: shell trả về: " + out);
        android().pressKey(new io.appium.java_client.android.nativekey.KeyEvent(io.appium.java_client.android.nativekey.AndroidKey.BACK));
    }

    @Test(description = "mobile: deepLink với package, mở activity VerifyAnotherDeviceActivity của Settings")
    public void deepLinkWithPackage() {
        new SettingsScreen(driver).waitLoaded();
        android().executeScript("mobile: deepLink", java.util.Map.of("url", "transparency://challenge", "package", SETTINGS_PACKAGE));
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> android().currentActivity().contains("VerifyAnotherDeviceActivity"));
        Assert.assertEquals(android().getCurrentPackage(), SETTINGS_PACKAGE);
    }

    @Test(description = "isKeyboardShown / hideKeyboard khi focus ô tìm kiếm của Settings")
    public void keyboardShownAndHide() {
        new SettingsScreen(driver).waitLoaded();
        driver.findElement(AppiumBy.id(SETTINGS_PACKAGE + ":id/search_bar_title")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> android().isKeyboardShown());
        Assert.assertTrue(android().isKeyboardShown());
        android().hideKeyboard();
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> !android().isKeyboardShown());
        Assert.assertFalse(android().isKeyboardShown());
    }

    private void waitFor(java.util.function.Function<org.openqa.selenium.WebDriver, Boolean> condition) {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(condition::apply);
    }
}
