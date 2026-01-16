package com.serenitydojo.playwright.toolsshop.catalog;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.HeadlessChromeOptions;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.CartLineItem;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.CheckoutCart;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.NavBar;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.ProductDetails;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.ProductList;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.SearchComponent;
import com.serenitydojo.playwright.toolsshop.fixtures.TakesFinalScreenshot;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.nio.file.Paths;
import java.util.List;

@DisplayName("Shopping Cart")
@Feature("Shopping Cart")
@UsePlaywright(HeadlessChromeOptions.class)
public class AddToCardTest implements TakesFinalScreenshot {

    SearchComponent searchComponent;
    ProductList productList;
    ProductDetails productDetails;
    NavBar navBar;
    CheckoutCart checkoutCart;

    @BeforeEach
    void openHomePage(Page page) {
        searchComponent = new SearchComponent(page);
        productList = new ProductList(page);
        productDetails = new ProductDetails(page);
        checkoutCart = new CheckoutCart(page);
        navBar = new NavBar(page);
        navBar.openHomePage();
    }

    @BeforeEach
    void setUpTrace(Page page) {
        page.context().tracing().start(
                new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true)
        );
    }

    @AfterEach
    void recordTrace(Page page, TestInfo testInfo) {
        String traceName = testInfo.getDisplayName().replace(" ", "-").toLowerCase();
        page.context().tracing().stop(
                new Tracing.StopOptions()
                        .setPath(Paths.get("target/traces/trace-" + traceName + ".zip"))
        );
    }

    @Test
    @Story("Checking out a product")
    @DisplayName("Checking out a single item")
    void whenCheckingOutASingleItem() {
        searchComponent.searchBy("pliers");
        productList.viewProductDetails("Combination Pliers");

        productDetails.increaseQuantityBy(2);
        productDetails.addToCart();

        navBar.openCart();

        List<CartLineItem> lineItems = checkoutCart.getLineItems();

        Assertions.assertThat(lineItems)
                .hasSize(1)
                .first()
                .satisfies(item -> {
                    Assertions.assertThat(item.title()).contains("Combination Pliers");
                    Assertions.assertThat(item.quantity()).isEqualTo(3);
                    Assertions.assertThat(item.total()).isEqualTo(item.quantity() * item.price());
                });
    }

    @Test
    @Story("Checking out a product")
    @DisplayName("Checking out multiple items")
    void whenCheckingOutMultipleItems() {
        navBar.openHomePage();
        productList.viewProductDetails("Bolt Cutters");
        productDetails.increaseQuantityBy(2);
        productDetails.addToCart();

        navBar.openHomePage();
        productList.viewProductDetails("Claw Hammer");
        productDetails.addToCart();

        navBar.openCart();
        List<CartLineItem> lineItems = checkoutCart.getLineItems();

        Assertions.assertThat(lineItems).hasSize(2);

        List<String> productNames = lineItems.stream().map(CartLineItem::title).toList();

        Assertions.assertThat(productNames).contains("Bolt Cutters", "Claw Hammer");

        Assertions.assertThat(lineItems)
                .allSatisfy(item -> {
                    Assertions.assertThat(item.quantity()).isGreaterThanOrEqualTo(1);
                    Assertions.assertThat(item.price()).isGreaterThanOrEqualTo(0.0);
                    Assertions.assertThat(item.total()).isGreaterThanOrEqualTo(0.0);
                    Assertions.assertThat(item.total()).isEqualTo(item.quantity() * item.price());
                });
    }
}
