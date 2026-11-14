package com.qakit.appium.screens;

import com.qakit.appium.pages.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

/**
 * Screen Object dùng chung cho Android và iOS (1 class, 2 bộ locator).
 * Các accessibility id bên dưới là PLACEHOLDER: thay bằng id của app thật,
 * kiểm tra bằng Appium Inspector hoặc driver.getPageSource() trước khi viết locator.
 */
public class LoginScreen extends BasePage {

    @AndroidFindBy(accessibility = "username_input")
    @iOSXCUITFindBy(accessibility = "username_input")
    private WebElement usernameInput;

    @AndroidFindBy(accessibility = "password_input")
    @iOSXCUITFindBy(accessibility = "password_input")
    private WebElement passwordInput;

    @AndroidFindBy(accessibility = "login_button")
    @iOSXCUITFindBy(accessibility = "login_button")
    private WebElement loginButton;

    @AndroidFindBy(accessibility = "error_message")
    @iOSXCUITFindBy(accessibility = "error_message")
    private WebElement errorMessage;

    public LoginScreen(AppiumDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(loginButton);
    }

    public void login(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        hideKeyboard();
        tap(loginButton);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }
}
