package com.example.selenium.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.selenium.components.HeaderComponent;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public final HeaderComponent header;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.header = new HeaderComponent(driver);
    }

    protected void openUrl(String url) {
        driver.get(url);
        waitForAmazonSearchBox();
    }

    /* Hack, useful only for Amazon */
    protected void waitForAmazonSearchBox() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("twotabsearchtextbox")));
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public ResultsPage search(String item) {
        header.searchFor(item);
        return new ResultsPage(driver);
    }

}
