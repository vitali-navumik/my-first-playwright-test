package com.serenitydojo.playwright;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightRestAPITest {
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
                        .setArgs(Arrays.asList("--no-sandbox",
                                "--disable-extensions",
                                "--disable-gpu"))
        );
    }

    @BeforeEach
    void setUp() {
        browserContext = browser.newContext();
        page = browserContext.newPage();

        page.navigate("https://practicesoftwaretesting.com");
        page.getByTestId("product-name").nth(1).waitFor();
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

    @DisplayName("Playwright allows us to mock out API responses")
    @Nested
    class MockingAPIResponses {

        @Test
        @DisplayName("When a search returns a single product")
        void whenASingleItemIsFound() {
            // https://api.practicesoftwaretesting.com/products/search?q=Pliers
            page.route("**/products/search?q=Pliers", route ->
                    route.fulfill(new Route.FulfillOptions()
                            .setStatus(200)
                            .setBody(MockSearchResponses.RESPONSE_WITH_A_SINGLE_ENTRY))
            );
            //  page.navigate("https://practicesoftwaretesting.com");
            page.getByPlaceholder("Search").fill("Pliers");
            page.getByPlaceholder("Search").press("Enter");

            assertThat(page.getByTestId("product-name")).hasCount(1);
            assertThat(page.getByTestId("product-name")).hasText("Super Pliers");
        }

        @Test
        @DisplayName("When a search returns no products")
        void whenNoItemsAreFound() {
            page.route("**/products/search?q=Pliers", route ->
                    route.fulfill(new Route.FulfillOptions()
                            .setStatus(200)
                            .setBody(MockSearchResponses.RESPONSE_WITH_NO_ENTRIES))
            );
            //page.navigate("https://practicesoftwaretesting.com");
            page.getByPlaceholder("Search").fill("Pliers");
            page.getByPlaceholder("Search").press("Enter");

            assertThat(page.getByTestId("product-name")).hasCount(0);
            assertThat(page.getByTestId("search_completed")).hasText("There are no products found.");
        }
    }

    @Nested
    class MakingAPICalls {

        record Product(String name, Double price) {
        }

        private static APIRequestContext requestContext;

        @BeforeAll
        public static void setupRequestContext() {
            requestContext = playwright.request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL("https://api.practicesoftwaretesting.com")
                            .setExtraHTTPHeaders(new HashMap<>() {{
                                put("Accept", "application/json");
                            }})
            );
        }

        @DisplayName("Check presence of known products")
        @ParameterizedTest(name = "Checking product {0}")
        @MethodSource("products")
        void checkKnownProduct(Product product) {
            page.fill("[placeholder='Search']", product.name);
            // page.getByPlaceholder("Search").fill(product.name);
            page.click("button:has-text('Search')");
            // page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Search")).click();

            Locator productCard = page.locator(".card")
                    .filter(
                            new Locator.FilterOptions()
                                    .setHasText(product.name)
                                    .setHasText(Double.toString(product.price))
                    );
            assertThat(productCard).isVisible();
        }

        static Stream<Product> products() {
            APIResponse response = requestContext.get("/products?page=2");
            Assertions.assertThat(response.status()).isEqualTo(200);

            JsonObject jsonObject = new Gson().fromJson(response.text(), JsonObject.class);
            JsonArray data = jsonObject.getAsJsonArray("data");

            return data.asList().stream()
                    .map(jsonElement -> {
                        JsonObject productJson = jsonElement.getAsJsonObject();
                        return new Product(
                                productJson.get("name").getAsString(),
                                productJson.get("price").getAsDouble()
                        );
                    });
        }
    }
}
