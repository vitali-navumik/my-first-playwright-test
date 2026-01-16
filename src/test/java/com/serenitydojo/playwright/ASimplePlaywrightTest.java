package com.serenitydojo.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ASimplePlaywrightTest {
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;

    Page page;

    @BeforeAll
    public static void setUpBrowser() {
//        playwright = Playwright.create();
//        browser = playwright.chromium().launch(
//                new BrowserType.LaunchOptions()
//                        .setHeadless(true)
//                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
//        );
//        browserContext = browser.newContext();
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(true)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
        browserContext = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        );
    }

    @BeforeEach
    public void setUp() {
        page = browserContext.newPage();
    }

    @AfterAll
    public static void tearDown() {
        browser.close();
        playwright.close();
    }

    @Test
    void shouldShowThePageTitle() {
        page.navigate("https://practicesoftwaretesting.com/");
        String title = page.title();

        Assertions.assertThat(title.contains("Practice Software Testing"));
    }

    @Test
    void shouldSearchByKeyword() {
        page.navigate("https://practicesoftwaretesting.com/");
        page.locator("[placeholder=Search]").fill("Pliers");
        page.locator("button:has-text('Search')").click();

        int matchingSearchResults = page.locator(".card").count();

        Assertions.assertThat(matchingSearchResults > 0);
    }
}
