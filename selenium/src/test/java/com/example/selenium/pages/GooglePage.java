package com.example.selenium.pages;

import org.openqa.selenium.WebDriver;

public class GooglePage extends BasePage {

    private static final String URL = "https://www.google.com";

    public GooglePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openUrl(URL);
    }
}
