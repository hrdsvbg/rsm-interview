package com.example.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SearchFunctionalityTest extends BaseTest {

    @Test
    void searchForItemWithoutCategory() {
        resultsPage = amazonPage.search("RTX 5080");
        String title = resultsPage.getTitle();
        assertTrue(title.contains("RTX 5080"), "Title must contain 'RTX 5080'");
        assertTrue(resultsPage.getResultsCount() > 0, "There must be at least one result");
    }

    @Test
    void searchWithoutResults() {
        resultsPage = amazonPage.search("zxczxczxczxczxczxczxczxczxczxc");
        assertTrue(resultsPage.getResultsCount() == 0, "There must be no results");
    }

    @Test
    void searchReturnsRelevantProducts() {
        resultsPage = amazonPage.search("Harry Potter");
        assertTrue(resultsPage.getResultsCount() > 0, "There must be at least one result");
        for (int i = 0; i < 3; i++) {
            assertTrue(resultsPage.getResult(i).getTitle().contains("Harry Potter"),
                    "Result " + i + " must contain 'Harry Potter'");
        }
    }

    @Test
    void multipleSearching() {
        resultsPage = amazonPage.search("RTX 5080");
        assertTrue(resultsPage.getResultsCount() > 0, "There must be at least one result");
        resultsPage = amazonPage.search("Harry Potter");
        assertTrue(resultsPage.getResultsCount() > 0, "There must be at least one result");
    }

    // TC: Search for an item in the wrong category, "RTX 5080" in "Books"
    // Expected no resutls in books and results from all categories
}
