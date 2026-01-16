package com.serenitydojo.playwright.toolsshop.cucumber.stepdefinitions;


import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;

import java.util.Arrays;

public class PlaywrightCucumberFixtures {
    private static final ThreadLocal<Playwright> playwright
            = ThreadLocal.withInitial(() -> {
                Playwright playwright = Playwright.create();
                playwright.selectors().setTestIdAttribute("data-test");
                return playwright;
            }
    );

    private static final ThreadLocal<Browser> browser = ThreadLocal.withInitial(() ->
            playwright.get().chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
                            .setArgs(Arrays.asList(
                                    "--no-sandbox",
                                    "--disable-extensions",
                                    "--disable-gpu",
                                    "--start-maximized"
                            ))
            )
    );

    private static final ThreadLocal<BrowserContext> browserContext = new ThreadLocal<>();

    private static final ThreadLocal<Page> page = new ThreadLocal<>();

    @Before(order = 100)
    public void setUpBrowserContext() {
//        boolean isCI = System.getenv("CI") != null;
//        if (isCI) {
//            browserContext.set(browser.get().newContext(
//                    new Browser.NewContextOptions().setViewportSize(1920, 1080)
//            ));
//        } else {
//            browserContext.set(browser.get().newContext(
//                    new Browser.NewContextOptions().setViewportSize(null)
//            ));
//        }
        boolean isCI = System.getenv("CI") != null;

        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

        if (isCI) {
            browserContext.set(browser.get().newContext(
                    new Browser.NewContextOptions()
                            .setUserAgent(userAgent)
                            .setViewportSize(1920, 1080)
            ));
        } else {
            browserContext.set(browser.get().newContext(
                    new Browser.NewContextOptions()
                            .setUserAgent(userAgent)
                            .setViewportSize(null)
            ));
        }

        page.set(browserContext.get().newPage());
    }

    @After
    public void closeContext() {
        browserContext.get().close();
    }

    @AfterAll
    public static void tearDown() {
        browser.get().close();
        browser.remove();

        playwright.get().close();
        playwright.remove();
    }

    public static Page getPage() {
        return page.get();
    }

    public static BrowserContext getBrowserContext() {
        return browserContext.get();
    }
}
