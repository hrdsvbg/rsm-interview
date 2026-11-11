package com.example.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShoppingCartPageTest extends BaseTest {

    @BeforeEach
    void setUpResultItem() {
        resultsPage = amazonPage.search("Harry Potter and the Cursed Child");
        productPage = resultsPage.getResult(0).openVersion("Paperback");
        productPage.pressAddToBasket();
        shoppingCartPage = productPage.header.openShoppingCart();
        item = shoppingCartPage.getItems().get(0);
    }

    @Test
    void shoppingCartContainsAnItem() {
        assertEquals(1, shoppingCartPage.getItems().size(), "Must contain a single item");
    }

    @Test
    void itemTitleMatches() {
        assertEquals(EXPECTED_PRODUCT_TITLE, item.getTitle());
    }

    @Test
    void priceAndCurrency() {
        assertTrue(item.getCurrency() != null && !item.getCurrency().isEmpty(), "Currency must not be empty");
        assertTrue(item.getPrice() > 0, "Price must be greater than 0");
        assertTrue(item.getCurrency().equals("EUR"), "Currency must be EUR");
    }

    @Test
    void increaseQuantity() {
        item.increaseQuantity();
        item = shoppingCartPage.getItems().get(0);
        assertEquals(2, item.getQuantity(), "Quantity is 2");
        // Changing the quantity results in quanitity element showing an animation
        // Additional check - double the amount in panel on the right
    }

    @Test
    void deleteItem() {
        item.delete();
        assertEquals(0, shoppingCartPage.getItems().size(), "No items");
    }

    // TC: Check caching - have an item in the cart, navigate to another page, 
    // return and check that the item is still in the cart

    // Other test cases:
    // - delete item by reducing quantity
    // - "Save for later" and "Share"
    // - proceed to checkout

}
