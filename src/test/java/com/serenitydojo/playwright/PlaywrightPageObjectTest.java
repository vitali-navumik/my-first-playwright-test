package com.serenitydojo.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.CartLineItem;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.CheckoutCart;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.NavBar;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.ProductDetails;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.ProductList;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.SearchComponent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightPageObjectTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(Arrays.asList(
                                "--no-sandbox",
                                "--disable-extensions",
                                "--disable-gpu"
                        ))
        );
    }

    @BeforeEach
    void setUp() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @AfterEach
    void closeContext() {
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void openHomePage() {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @Nested
    class WhenSearchingProductsByKeyword {

        @DisplayName("Without Page Objects")
        @Test
        void withoutPageObjects() {
            page.waitForResponse("**/products/search?q=tape", () -> {
                page.getByPlaceholder("Search").fill("tape");
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            });
            List<String> matchingProducts = page.getByTestId("product-name").allInnerTexts();
            Assertions.assertThat(matchingProducts)
                    .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");

        }

        @DisplayName("With Page Objects")
        @Test
        void withPageObjects() {
            SearchComponent searchComponent = new SearchComponent(page);
            ProductList productList = new ProductList(page);

            searchComponent.searchBy("tape");
            var matchingProducts = productList.getProductNames();

            Assertions.assertThat(matchingProducts)
                    .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
        }
    }

    @Nested
    class WhenAddingItemsToTheCart {
        SearchComponent searchComponent;
        ProductList productList;
        ProductDetails productDetails;
        NavBar navBar;
        CheckoutCart checkoutCart;

        @BeforeEach
        void setUp() {
            searchComponent = new SearchComponent(page);
            productList = new ProductList(page);
            productDetails = new ProductDetails(page);
            navBar = new NavBar(page);
            checkoutCart = new CheckoutCart(page);
        }

        @DisplayName("Without Page Objects")
        @Test
        void withoutPageObjects() {
            // Search for pliers
            page.waitForResponse("**/products/search?q=pliers", () -> {
                page.getByPlaceholder("Search").fill("pliers");
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            });
            // Show details page
            page.locator(".card").getByText("Combination Pliers").click();

            // Increase cart quantity
            page.getByTestId("increase-quantity").click();
            page.getByTestId("increase-quantity").click();
            // Add to cart
            page.getByText("Add to cart").click();
            page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("3"));

            // Open the cart
            page.getByTestId("nav-cart").click();

            // check cart contents
            assertThat(page.locator(".product-title").getByText("Combination Pliers")).isVisible();
            assertThat(page.getByTestId("cart-quantity").getByText("3")).isVisible();
        }

        @Test
        void withPageObjects() {
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
}