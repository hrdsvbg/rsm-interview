package com.example.selenium.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.example.selenium.components.SearchResultComponent;

public class ResultsPage extends BasePage {

    private final By resultsLocator = By.xpath("//*[@data-component-type='s-search-result']");

    private final By noResultsLocator = By.xpath("//span[normalize-space()='No results for your search query.']");

    // public ResultsPage(WebDriver driver) {
    //     super(driver);
    //     // wait.until(ExpectedConditions.presenceOfElementLocated(resultsLocator));
    // }

    // public SearchResultComponent getResult(int index) {
    //     List<WebElement> results = driver.findElements(resultsLocator);
    //     return new SearchResultComponent(results.get(index), driver);
    // }

    // public int getResultsCount() {
    //     return driver.findElements(resultsLocator).size();
    // }

    public ResultsPage(WebDriver driver) {
        super(driver);
        waitForSearchResponse();
    }

    private void waitForSearchResponse() {
    wait.until(ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(resultsLocator),
            ExpectedConditions.presenceOfElementLocated(noResultsLocator)
    ));
}


    public SearchResultComponent getResult(int index) {
        List<WebElement> results = driver.findElements(resultsLocator);
        return new SearchResultComponent(results.get(index), driver);
    }

    public int getResultsCount() {
        return driver.findElements(resultsLocator).size();
    }

    public boolean hasResults() {
        return getResultsCount() > 0;
    }

    public boolean hasNoResults() {
        return driver.findElements(noResultsLocator).size() > 0;
    }
}