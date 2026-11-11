package com.example.selenium.components;

import com.example.selenium.pages.ProductPage;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchResultComponent {

    // Prefer not to declare locators here, as they are relative to the root element
    // of this component

    private final WebElement root;
    private final WebDriver driver;

    public SearchResultComponent(WebElement root, WebDriver driver) {
        this.root = root;
        this.driver = driver;
    }

    public String getTitle() {
        return root.findElement(By.xpath(".//h2/span")).getText();
    }

    public float getPrice() {

        float priceWhole = Float.parseFloat(root.findElement(
                By.xpath(".//span[@class='a-price-whole']")).getText());

        float priceFraction = Float.parseFloat(root.findElement(
                By.xpath(".//span[@class='a-price-fraction']")).getText());

        return priceWhole + priceFraction / 100;
    }

    public String getCurrency() {
        return root.findElement(By.xpath(".//span[@class='a-price-symbol']")).getText();
    }

    public List<String> getVersions() {
        List<WebElement> versionsElements = root.findElements(
                By.xpath(".//a[contains(@class,'a-text-bold') and contains(@href,'/dp/')]"));
        return versionsElements.stream()
                .map(WebElement::getText)
                .toList();
    }

    public ProductPage openVersion(String version) {

        List<WebElement> versionsElements = root.findElements(
                By.xpath(".//a[contains(@class,'a-text-bold') and contains(@href,'/dp/')]"));

        versionsElements.stream()
                .filter(element -> element.getText().trim().equalsIgnoreCase(version))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Version not found: " + version))
                .click();

        return new ProductPage(driver);
    }

}
