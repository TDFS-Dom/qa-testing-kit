# Quy Tắc Dành Riêng Cho Selenium WebDriver

> Áp dụng khi tự động hóa browser với Java và Selenium WebDriver.

## 1. Thứ Tự Ưu Tiên Locator

1. `id` — Nhanh nhất, unique nhất
2. `data-testid` / `data-test` / `data-qa` — Thuộc tính chuyên cho test
3. `name` — Thuộc tính HTML chuẩn
4. `cssSelector` — Linh hoạt, nhanh
5. `xpath` — Lựa chọn cuối cùng

```java
// Đúng
driver.findElement(By.id("login-btn"));
driver.findElement(By.cssSelector("button[data-testid='submit-btn']"));

// NGHIÊM CẤM: XPath tuyệt đối
driver.findElement(By.xpath("//div[3]/div[2]/form/div[1]/button"));
```

## 2. Chiến Lược Chờ Đợi

**NGHIÊM CẤM:** `Thread.sleep()` — Trong mọi trường hợp

**SỬ DỤNG:** `WebDriverWait` + `ExpectedConditions`:

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profile")));
wait.until(ExpectedConditions.elementToBeClickable(By.id("submit-btn")));
wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "Thành công"));
wait.until(ExpectedConditions.urlContains("/dashboard"));
```

## 3. Thiết Lập Browser

- Viewport: `1920x1080` khi debug
- Headed mode bắt buộc khi debug
- Headless chỉ khi test đã PASS hoặc trong CI/CD

```java
driver.manage().window().setSize(new Dimension(1920, 1080));
```

## 4. Cấu Trúc Test (TestNG)

```java
public class LoginTest extends BaseTest {
    @BeforeMethod
    public void setUp() { /* Navigate, setup data */ }

    @Test(groups = {"smoke", "regression"})
    public void testLoginWithValidCredentials() {
        // Arrange → Act → Assert
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(email, "ValidPass@123");
        Assert.assertTrue(dashboard.isDisplayed(), "Dashboard phải hiển thị");
    }

    @AfterMethod
    public void tearDown() { /* Cleanup */ }
}
```

## 5. Assertions

- Sử dụng TestNG Assertions
- Luôn thêm **message mô tả**:
  ```java
  Assert.assertEquals(actualTitle, "Dashboard", "Tiêu đề trang phải là Dashboard");
  ```
- Mỗi test method phải có ít nhất **1 assertion** ở cuối
