package com.example.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

public class ResultItemTest extends BaseTest {

    @BeforeEach
    void setUpResultItem() {
        resultsPage = amazonPage.search(SEARCH_TERM);
        searchResult = resultsPage.getResult(0);
    }

    @Test
    void itemHasAtleastOneVersion() {
        searchResult.getVersions().forEach(version -> {
            assertTrue(
                    version.equals("Paperback") || version.equals("Hardcover")
                            || version.equals("Kindle Edition with Audio/Video"),
                    "Version must be Paperback, Hardcover or Kindle");
        });
    }

    @Test
    void itemHasThreeVersions() {
        assertTrue(searchResult.getVersions().size() == 3, "There must be three versions");
    }

    @Test
    void priceAndCurrency() {
        assertTrue(searchResult.getCurrency() != null && !searchResult.getCurrency().isEmpty(),
                "Currency must not be empty");
        assertTrue(searchResult.getPrice() > 0, "Price must be greater than 0");
        assertTrue(searchResult.getCurrency().equals("EUR"), "Currency must be EUR");
    }

    @Test
    void itemVersionOpensProductPage() {
        productPage = searchResult.openVersion("Paperback");
        assertTrue(productPage.getTitle().contains("Harry Potter and the Cursed Child"));
        assertEquals(EXPECTED_PRODUCT_TITLE, productPage.getProductTitle());
    }
        // This TC can be parametrized if we know the avalable version

    // Other test case ideas:
    // - Author
    // - Rating and reviews (review redirects)
    // - Delivery options and discounts
    // - Availability

}
