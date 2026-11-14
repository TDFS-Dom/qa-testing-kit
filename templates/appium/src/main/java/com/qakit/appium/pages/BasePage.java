package com.qakit.appium.pages;

import com.qakit.appium.config.AppConfig;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Lớp cơ sở dùng chung. Màn hình cụ thể là Screen Object, đặt tên có hậu tố Screen (vd. LoginScreen)
 * và kế thừa lớp này. Chỉ dùng smart wait, không Thread.sleep.
 */
public abstract class BasePage {

    protected final AppiumDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(AppiumDriver driver) {
        this.driver = driver;
        Duration timeout = Duration.ofSeconds(AppConfig.waitTimeoutSeconds());
        this.wait = new WebDriverWait(driver, timeout);
        PageFactory.initElements(new AppiumFieldDecorator(driver, timeout), this);
    }

    protected WebElement waitVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected void tap(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    protected void type(WebElement element, String text) {
        WebElement target = waitVisible(element);
        target.clear();
        target.sendKeys(text);
    }

    protected String getText(WebElement element) {
        return waitVisible(element).getText();
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    /** hideKeyboard() nằm trên AndroidDriver/IOSDriver (không có trên AppiumDriver), nên cần ép kiểu. */
    protected void hideKeyboard() {
        if (driver instanceof AndroidDriver android) {
            if (android.isKeyboardShown()) {
                android.hideKeyboard();
            }
        } else if (driver instanceof IOSDriver ios) {
            if (ios.isKeyboardShown()) {
                ios.hideKeyboard();
            }
        }
    }
}
