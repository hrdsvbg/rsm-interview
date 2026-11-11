package com.example.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AssignedTaskFlow extends BaseTest {

    @Test
    void assignmentFlow() {
        resultsPage = amazonPage.search(SEARCH_TERM);
        searchResult = resultsPage.getResult(0);

        assertTrue(searchResult.getTitle().contains(SEARCH_TERM), "Title must contain '" + SEARCH_TERM + "'");
        assertEquals(EXPECTED_PRODUCT_TITLE, searchResult.getTitle(),
                "Product title must match expected title");

        float listedPrice = searchResult.getPrice();
        String listedCurrency = searchResult.getCurrency();
        assertTrue(listedPrice > 0, "Price must be greater than 0");
        // assertEquals("EUR", listedCurrency, "Currency must be in EUR"); 

        assertTrue(searchResult.getVersions().size() > 0, "There must be at least one version available");
        assertTrue(searchResult.getVersions().contains("Paperback"),
                "There must be a paperback version available");

        productPage = searchResult.openVersion("Paperback");
        assertEquals(EXPECTED_PRODUCT_TITLE, productPage.getProductTitle(), "Product title must match expected title");

        productPage.pressAddToBasket();
        // At this point the website shows a page "Product added to basket" with a
        // button "Go to basket". Intentionaly going to the Shopping Cart page since it
        // is not mentioned in the assignment
        shoppingCartPage = productPage.header.openShoppingCart();

        assertTrue(shoppingCartPage.getItems().size() == 1, "There must be one item in the cart");

        item = shoppingCartPage.getItems().get(0);

        assertEquals(EXPECTED_PRODUCT_TITLE, item.getTitle(), "Title must match expected title");
        assertEquals("Paperback", item.getFormat(), "Format must be Paperback");
        assertEquals(1, item.getQuantity(), "Quantity must be 1");
        assertEquals(listedPrice, item.getPrice(), "Price must match listed price before adding to cart");
        assertEquals(listedCurrency, item.getCurrency(),
                "Item currency must match listed currency before adding to cart");

        assertFalse(item.isItMarkedAsGift(), "Must not be marked as a gift by default");
        item.markAsGift();
        assertTrue(item.isItMarkedAsGift(), "Item must be marked as a gift after calling markAsGift()");

    }

}
