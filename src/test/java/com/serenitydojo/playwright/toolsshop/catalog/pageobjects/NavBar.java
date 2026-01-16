package com.serenitydojo.playwright.toolsshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import com.serenitydojo.playwright.toolsshop.fixtures.ScreenshotManager;
import io.qameta.allure.Step;

public class NavBar {
    private final Page page;

    public NavBar(Page page) {
        this.page = page;
    }

    @Step("Open the shopping cart")
    public void openCart() {
        page.getByTestId("nav-cart").click();
        ScreenshotManager.takeScreenshot(page, "Shopping cart");
    }

    @Step("Open home page")
    public void openHomePage() {
        page.navigate("https://practicesoftwaretesting.com");
        page.getByTestId("product-name").first().waitFor();
        ScreenshotManager.takeScreenshot(page, "Home page");
    }

    @Step("Open contact page")
    public void openContactPage() {
        page.navigate("https://practicesoftwaretesting.com/contact");
        page.getByTestId("first-name").waitFor();
        ScreenshotManager.takeScreenshot(page, "Contact page");
    }
}
