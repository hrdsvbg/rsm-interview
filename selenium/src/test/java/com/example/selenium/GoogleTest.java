package com.example.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.selenium.pages.GooglePage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

class GoogleTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void opensGoogle() {
        GooglePage googlePage = new GooglePage(driver);
        googlePage.open();

        assertEquals("Google", googlePage.getTitle());
    }
}
