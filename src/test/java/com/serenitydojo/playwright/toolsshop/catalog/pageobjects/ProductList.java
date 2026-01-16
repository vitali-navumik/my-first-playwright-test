package com.serenitydojo.playwright.toolsshop.catalog.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.serenitydojo.playwright.toolsshop.domain.ProductSummary;
import com.serenitydojo.playwright.toolsshop.fixtures.ScreenshotManager;
import io.qameta.allure.Step;

import java.util.List;

public class ProductList {
    private final Page page;

    public ProductList(Page page) {
        this.page = page;
    }

    public List<String> getProductNames() {
        //return page.getByTestId("product-name").allInnerTexts();
        Locator products = page.getByTestId("product-name");

        products.first().waitFor();

        return products.allInnerTexts();
    }

    public List<ProductSummary> getProductSummaries() {
        return page.locator(".card").all().stream()
                .map(productCard -> new ProductSummary(
                        productCard.getByTestId("product-name").textContent().strip(),
                        productCard.getByTestId("product-price").textContent()
                ))
                .toList();
    }

    @Step("View product details")
    public void viewProductDetails(String productName) {
        ScreenshotManager.takeScreenshot(page, "View product details for " + productName);
        page.locator(".card").getByText(productName,
                new Locator.GetByTextOptions().setExact(true)).click();
    }

    public String getSearchCompletedMessage() {

        return page.getByTestId("search_completed").textContent();
    }
}
