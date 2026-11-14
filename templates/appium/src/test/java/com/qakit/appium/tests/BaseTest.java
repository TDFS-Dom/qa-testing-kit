package com.qakit.appium.tests;

import com.qakit.appium.config.AppConfig;
import com.qakit.appium.utils.DriverFactory;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseTest {

    protected AppiumDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.createDriver(AppConfig.platform());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE && driver != null) {
                saveFailureArtifacts(result.getName());
            }
        } finally {
            DriverFactory.quitDriver();
        }
    }

    /** Lưu screenshot + page source vào target/artifacts/<test>_<timestamp>/ để debug lỗi. */
    private void saveFailureArtifacts(String testName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path dir = Paths.get("target", "artifacts", testName + "_" + timestamp);
        try {
            Files.createDirectories(dir);
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(dir.resolve("screenshot.png"), png);
            Files.writeString(dir.resolve("page-source.xml"), driver.getPageSource());
        } catch (IOException e) {
            System.err.println("Không lưu được artifact lỗi: " + e.getMessage());
        }
    }
}
