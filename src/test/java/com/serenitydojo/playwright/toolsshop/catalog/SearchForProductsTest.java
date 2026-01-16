package com.serenitydojo.playwright.toolsshop.catalog;

import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.CheckoutCart;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.NavBar;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.ProductDetails;
import com.serenitydojo.playwright.toolsshop.fixtures.PlaywrightTestCase;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.ProductList;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.SearchComponent;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Searching for products")
@Feature("Product Catalog")
public class SearchForProductsTest extends PlaywrightTestCase {
    SearchComponent searchComponent;
    ProductList productList;
    ProductDetails productDetails;
    NavBar navBar;
    CheckoutCart checkoutCart;

    @BeforeEach
    void openHomePage() {
        navBar = new NavBar(page);
        navBar.openHomePage();
        searchComponent = new SearchComponent(page);
        productList = new ProductList(page);
    }

    @Nested
    @DisplayName("Searching by keyword")
    @Story("Searching for products")
    class SearchingByKeyword {

        @DisplayName("When there are matching results")
        @Test
        void whenSearchingByKeyword() {
            searchComponent.searchBy("tape");
            var matchingProducts = productList.getProductNames();

            Assertions.assertThat(matchingProducts)
                    .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
        }

        @DisplayName("When there are no matching results")
        @Test
        void whenThereIsNoMatchingProduct() {
            searchComponent.searchBy("unknown");
            var matchingProducts = productList.getProductNames();

            Assertions.assertThat(matchingProducts).isEmpty();
            Assertions.assertThat(productList.getSearchCompletedMessage()).isEqualTo("There are no products found.");
        }

        @DisplayName("When the user clears a previous search results")
        @Test
        void clearingTheSearchResults() {
            searchComponent.searchBy("saw");
            var matchingFilteredProducts = productList.getProductNames();

            Assertions.assertThat(matchingFilteredProducts).hasSize(2);

            searchComponent.clearSearch();
            var matchingProducts = productList.getProductNames();

            Assertions.assertThat(matchingProducts).hasSize(9);
        }
    }
}