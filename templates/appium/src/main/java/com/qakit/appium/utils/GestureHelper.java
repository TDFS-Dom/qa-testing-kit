package com.qakit.appium.utils;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

/**
 * Gesture theo chuẩn W3C Actions (PointerInput + Sequence). Thay thế TouchAction/MultiTouchAction đã bỏ.
 * Ý tưởng tham khảo skill "appium-mobile" của thetestingacademy (MIT), code viết lại cho java-client 9.x.
 */
public class GestureHelper {

    public enum Direction { UP, DOWN, LEFT, RIGHT }

    private static final PointerInput.Kind TOUCH = PointerInput.Kind.TOUCH;

    private final AppiumDriver driver;

    public GestureHelper(AppiumDriver driver) {
        this.driver = driver;
    }

    public void tap(int x, int y) {
        PointerInput finger = new PointerInput(TOUCH, "finger");
        Sequence tap = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(50)))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }

    public void tap(WebElement element) {
        Point center = center(element);
        tap(center.getX(), center.getY());
    }

    public void doubleTap(WebElement element) {
        Point c = center(element);
        PointerInput finger = new PointerInput(TOUCH, "finger");
        Sequence seq = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), c.getX(), c.getY()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, Duration.ofMillis(100)))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(seq));
    }

    public void longPress(WebElement element, Duration holdTime) {
        Point c = center(element);
        PointerInput finger = new PointerInput(TOUCH, "finger");
        Sequence seq = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), c.getX(), c.getY()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, holdTime))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(seq));
    }

    /** Vuốt theo hướng ngón tay di chuyển (UP = vuốt từ dưới lên, nội dung cuộn xuống). */
    public void swipe(Direction direction) {
        Dimension size = driver.manage().window().getSize();
        int midX = size.getWidth() / 2;
        int midY = size.getHeight() / 2;
        int left = (int) (size.getWidth() * 0.2);
        int right = (int) (size.getWidth() * 0.8);
        int top = (int) (size.getHeight() * 0.2);
        int bottom = (int) (size.getHeight() * 0.8);

        switch (direction) {
            case UP -> swipe(midX, bottom, midX, top);
            case DOWN -> swipe(midX, top, midX, bottom);
            case LEFT -> swipe(right, midY, left, midY);
            case RIGHT -> swipe(left, midY, right, midY);
        }
    }

    public void swipe(int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(TOUCH, "finger");
        Sequence seq = new Sequence(finger, 0)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(seq));
    }

    /** Chỉ Android: cuộn bằng UiScrollable tới element có text chỉ định. */
    public WebElement scrollToTextAndroid(String text) {
        String escaped = text.replace("\\", "\\\\").replace("\"", "\\\"");
        return driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                        + ".scrollIntoView(new UiSelector().text(\"" + escaped + "\"))"));
    }

    private Point center(WebElement element) {
        Rectangle r = element.getRect();
        return new Point(r.getX() + r.getWidth() / 2, r.getY() + r.getHeight() / 2);
    }
}
