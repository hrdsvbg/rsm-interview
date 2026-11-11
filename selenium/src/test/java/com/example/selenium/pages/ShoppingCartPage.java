package com.example.selenium.pages;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.example.selenium.components.CartItemComponent;

public class ShoppingCartPage extends BasePage {

    // private final WebDriver driver;

    public ShoppingCartPage(WebDriver driver) {
        super(driver);
    }

    public List<CartItemComponent> getItems() {
        List<WebElement> items = driver.findElements(
                By.xpath("//div[contains(@class,'sc-list-item') " +
                        "and @data-asin " +
                        "and not(@data-removed='true')]"));
        return items.stream()
                .map(item -> new CartItemComponent(item, driver))
                .toList();
    }

    public CartItemComponent getItemByTitle(String title) {
        return getItems().stream()
                .filter(item -> item.getTitle().equals(title))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cart item not found: " + title));
    }

}