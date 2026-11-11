package com.example.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LandingPageTest extends BaseTest {

    @Test
    void openAmazon() {
        String title = amazonPage.getTitle();
        assertTrue(title.contains("Amazon.co.uk"), "Title must contain 'Amazon.co.uk'");
    }

    // TC: Check that logo and other elements are present, and that the page is
    // loaded correctly

    // TC: Search field and dropdown with categories are present, search works and
    // results are displayed

    @Test
    void searchForItemWorks() {
        resultsPage = amazonPage.search("RTX 5080");
        String title = resultsPage.getTitle();
        assertTrue(title.contains("RTX 5080"), "Title must contain 'RTX 5080'");
        assertTrue(resultsPage.getResultsCount() > 0, "There must be at least one result");
    }

    @Test
    void openShoppingCart() {
        shoppingCartPage = amazonPage.header.openShoppingCart();
        assertTrue(shoppingCartPage.getTitle().contains("Shopping Basket"), "Title must contain 'Shopping Basket'");
        assertEquals(0, shoppingCartPage.getItems().size(), "Shopping cart must be empty");
    }



}
