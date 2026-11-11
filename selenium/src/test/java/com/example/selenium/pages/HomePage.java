package com.example.selenium.pages;

import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private static final String URL = "https://www.amazon.co.uk";

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        openUrl(URL);
    }
}
