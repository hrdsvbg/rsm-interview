package com.example.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.selenium.pages.ShoppingCartPage;

import java.time.Duration;

public class HeaderComponent {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By searchInput = By.id("twotabsearchtextbox");
    private final By searchSubmitButton = By.id("nav-search-submit-button");
    private final By shoppingCart = By.id("nav-cart");

    public HeaderComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void searchFor(String item) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
        input.clear();
        input.sendKeys(item);
        wait.until(ExpectedConditions.elementToBeClickable(searchSubmitButton)).click();
    }

    public ShoppingCartPage openShoppingCart() {
        wait.until(ExpectedConditions.elementToBeClickable(shoppingCart)).click();
        return new ShoppingCartPage(driver);
    }
}