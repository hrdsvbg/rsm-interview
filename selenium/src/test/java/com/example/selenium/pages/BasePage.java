package com.example.selenium.pages;

import org.openqa.selenium.WebDriver;

public abstract class BasePage {

    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    protected void openUrl(String url) {
        driver.get(url);
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
