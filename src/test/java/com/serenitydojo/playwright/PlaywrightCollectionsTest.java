package com.serenitydojo.playwright;


import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
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

public class PlaywrightCollectionsTest {
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;

    Page page;

    @BeforeAll
    public static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
        playwright.selectors().setTestIdAttribute("data-test");
    }

    @BeforeEach
    public void setUp() {
        // browserContext = browser.newContext();
        browserContext = browser.newContext(new Browser.NewContextOptions()
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"));
        page = browserContext.newPage();
        openPage();
    }

    @AfterEach
    void closeContext() {
        browserContext.close();
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        playwright.close();
    }

    private void openPage() {
        page.navigate("https://practicesoftwaretesting.com/");
        page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);
    }

    @DisplayName("Counting items in a list")
    @Test
    void countingItemsOnThePage() {
        int itemsOnThePage = page.locator(".card").count();

        Assertions.assertThat(itemsOnThePage).isGreaterThan(0);
    }

    @DisplayName("Finding the first matching item")
    @Test
    void findingTheFirstMatchingItem() {
        page.locator(".card").first().click();
    }

    @DisplayName("Finding the nth matching item")
    @Test
    void findingNthMatchingItem() {
        page.locator(".card").nth(2).click();
    }

    @DisplayName("Finding the last matching item")
    @Test
    void findingLastMatchingItem() {
        page.locator(".card").last().click();
    }

    @DisplayName("Finding text in a list")
    @Nested
    class FindingTheTextInAList {

        @DisplayName("and finding all the text values ")
        @Test
        void withAllTextContents() {
            List<String> itemNames = page.getByTestId("product-name").allTextContents();

            Assertions.assertThat(itemNames).contains(" Combination Pliers ",
                    " Pliers ",
                    " Bolt Cutters ",
                    " Long Nose Pliers ",
                    " Slip Joint Pliers ",
                    " Claw Hammer with Shock Reduction Grip ",
                    " Hammer ",
                    " Claw Hammer ",
                    " Thor Hammer ");
        }

        @DisplayName("and asserting with  hasText")
        @Test
        void withHasText() {
            assertThat(page.getByTestId("product-name"))
                    .hasText(new String[]{
                            " Combination Pliers ",
                            " Pliers ",
                            " Bolt Cutters ",
                            " Long Nose Pliers ",
                            " Slip Joint Pliers ",
                            " Claw Hammer with Shock Reduction Grip ",
                            " Hammer ",
                            " Claw Hammer ",
                            " Thor Hammer "
                    });
        }
    }
}
