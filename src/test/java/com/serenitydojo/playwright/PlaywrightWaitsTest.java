package com.serenitydojo.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(HeadlessChromeOptions.class)
public class PlaywrightWaitsTest {

    @Nested
    class WaitingForState {
        @BeforeEach
        void openHomePage(Page page) {
            page.navigate("https://practicesoftwaretesting.com/");
            page.waitForSelector("[data-test=product-name]");
        }

        @Test
        void shouldShowAllProductNames(Page page) {
            List<String> productNames = page.getByTestId("product-name").allInnerTexts();
            Assertions.assertThat(productNames).contains("Pliers", "Bolt Cutters", "Hammer");
        }

        @Test
        void shouldShowAllProductImages(Page page) {
            List<String> productImageTitles = page.locator(".card-img-top").all()
                    .stream()
                    .map(img -> img.getAttribute("alt"))
                    .toList();

            Assertions.assertThat(productImageTitles).contains("Pliers", "Bolt Cutters", "Hammer");
        }
    }

    @Nested
    class AutomaticWaits {
        @BeforeEach
        void openHomePage(Page page) {
            page.navigate("https://practicesoftwaretesting.com");
        }

        @Test
        @DisplayName("Should wait for the filter checkbox options to appear before clicking")
        void shouldWaitForTheFilterCheckboxes(Page page) {

            var screwdriverFilter = page.getByLabel("Screwdriver");
            screwdriverFilter.click();

            assertThat(screwdriverFilter).isChecked();
        }

        @Test
        @DisplayName("Should filter products by category")
        void shouldFilterProductsByCategory(Page page) {
            page.getByRole(AriaRole.MENUBAR).getByText("Categories").click();
            page.getByRole(AriaRole.MENUBAR).getByText("Power Tools").click();

            page.waitForSelector(".card",
                    new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(2000)
            );

            List<String> filteredProducts = page.getByTestId("product-name").allInnerTexts();

            Assertions.assertThat(filteredProducts).contains("Sheet Sander", "Belt Sander", "Circular Saw");
        }
    }

    @Nested
    class WaitingForElementsToAppearAndDisappear {
        @BeforeEach
        void openHomePage(Page page) {
            page.navigate("https://practicesoftwaretesting.com");
        }

        @Test
        @DisplayName("It should display a toaster message when an item is added to the cart")
        void shouldDisplayToasterMessage(Page page) {
            page.getByText("Bolt Cutters").click();
            page.getByText("Add to cart").click();

            // Wait for the toaster message to appear
            assertThat(page.getByRole(AriaRole.ALERT)).isVisible();
            assertThat(page.getByRole(AriaRole.ALERT)).hasText("Product added to shopping cart.");

            page.waitForCondition(() -> page.getByRole(AriaRole.ALERT).isHidden());
        }

        @Test
        @DisplayName("Should update the cart item count")
        void shouldUpdateCartItemCount(Page page) {
            page.getByText("Bolt Cutters").click();
            page.getByText("Add to cart").click();

            page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("1"));
            //page.waitForSelector("[data-test=cart-quantity]:has-text('1')");
        }
    }

    @Nested
    class WaitingForAPICalls {
        @BeforeEach
        void openHomePage(Page page) {
            page.navigate("https://practicesoftwaretesting.com");
        }

        @Test
        void sortByDescendingPrice(Page page) {

            // Sort by descending price
            // https://api.practicesoftwaretesting.com/products?page=0&sort=price,desc&between=price,1,100&is_rental=false
            page.waitForResponse("**/products?page=0&sort**",
                    () -> page.getByTestId("sort").selectOption("Price (High - Low)"));

            // Find all the prices on the page
            List<Double> productPrices = page.getByTestId("product-price")
                    .allInnerTexts()
                    .stream()
                    .map(WaitingForAPICalls::extractPrice)
                    .toList();

            // Are the prices in the correct order
            Assertions.assertThat(productPrices)
                    .isNotEmpty()
                    .isSortedAccordingTo(Comparator.reverseOrder());
        }

        private static double extractPrice(String price) {
            return Double.parseDouble(price.replace("$", ""));
        }
    }
}