package com.example.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductPage extends BasePage {

    private final By titleLocator = By.xpath("//*[@id=\"productTitle\"]");

    private final By addToBasketButton = By.xpath("//input[@id='add-to-cart-button']");

    public ProductPage(WebDriver driver) {
        super(driver);
        wait.until(ExpectedConditions.presenceOfElementLocated(titleLocator));
    }

    public String getProductTitle() {
        return driver.findElement(titleLocator).getText();
    }

    public void pressAddToBasket() {
        driver.findElement(addToBasketButton).click();
    }

}