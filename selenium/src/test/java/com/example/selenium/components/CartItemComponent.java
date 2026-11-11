package com.example.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartItemComponent {

    // Locators are relative to the root element of this component

    private final WebElement root;
    private final WebDriver driver;

    public CartItemComponent(WebElement root, WebDriver driver) {
        this.root = root;
        this.driver = driver;
    }

    public String getTitle() {
        WebElement title = root.findElement(
                By.xpath(".//span[contains(@class,'a-truncate-full')]"));
        return title.getAttribute("textContent").trim(); // Deprecated but works for this demo.
    }

    public String getAuthor() {
        String byAuthor = root.findElement(
                By.xpath(".//span[@data-csa-c-content-id='creator']")).getText();
        return byAuthor.replace("by ", "").trim();
    }

    public float getPrice() {

        String whole = root.findElement(
                By.xpath(".//span[contains(@class,'a-price-whole')]")).getText();

        String fraction = root.findElement(
                By.xpath(".//span[contains(@class,'a-price-fraction')]")).getText();

        return Float.parseFloat(whole + "." + fraction);
    }

    public String getCurrency() {
        return root.findElement(
                By.xpath(".//span[contains(@class,'a-price-symbol')]")).getText();
    }

    public String getFormat() {
        return root.findElement(
                By.xpath(".//span[@data-csa-c-content-id='binding']")).getText();
    }

    public String getAvailability() {
        return root.findElement(
                By.xpath(".//span[@data-csa-c-content-id='availability']")).getText();
    }

    public int getQuantity() {
        return Integer.parseInt(
                root.findElement(
                        By.xpath(".//*[@data-a-selector='inner-value']")).getText());
    }

    public void markAsGift() {
        WebElement giftCheckbox = root.findElement(
                By.xpath(".//div[contains(@class,'sc-item-gift-checkbox')]//input[@type='checkbox']"));

        if (!giftCheckbox.isSelected()) {
            root.findElement(
                    By.xpath(".//div[contains(@class,'sc-item-gift-checkbox')]//label")).click();
        }
    }

    public boolean isItMarkedAsGift() {
        return root.findElement(
                By.xpath(".//div[contains(@class,'sc-item-gift-checkbox')]//input[@type='checkbox']")).isSelected();
    }

    public void increaseQuantity() {
        root.findElement(
                By.xpath(".//button[@data-a-selector='increment']")).click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // For the sake of the scenario :)
            e.printStackTrace();
        }
    }

    public void decreaseQuantity() {
        root.findElement(
                By.xpath(".//button[@data-a-selector='decrement']")).click();
    }

    public void delete() {
        root.findElement(
                By.xpath(".//input[@data-feature-id='item-delete-button']")).click();
    }
}
