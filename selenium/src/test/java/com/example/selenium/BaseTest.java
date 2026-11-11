package com.example.selenium;

import com.example.selenium.components.CartItemComponent;
import com.example.selenium.components.SearchResultComponent;
import com.example.selenium.pages.HomePage;
import com.example.selenium.pages.ProductPage;
import com.example.selenium.pages.ResultsPage;
import com.example.selenium.pages.ShoppingCartPage;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public abstract class BaseTest {

    protected WebDriver driver;
    protected HomePage amazonPage;
    protected ResultsPage resultsPage;
    protected SearchResultComponent searchResult;
    protected ProductPage productPage;
    protected ShoppingCartPage shoppingCartPage;
    protected CartItemComponent item;

    protected static final String SEARCH_TERM = "Harry Potter and the Cursed Child";
    protected static final String EXPECTED_PRODUCT_TITLE = "Harry Potter and the Cursed Child - Parts One and Two: The Official Playscript of the Original West End Production";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        amazonPage = new HomePage(driver);
        amazonPage.open();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}