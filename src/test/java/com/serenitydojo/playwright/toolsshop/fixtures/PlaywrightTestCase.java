package com.serenitydojo.playwright.toolsshop.fixtures;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;

public abstract class PlaywrightTestCase {

    protected static ThreadLocal<Playwright> playwright
            = ThreadLocal.withInitial(() -> {
                Playwright playwright = Playwright.create();
                playwright.selectors().setTestIdAttribute("data-test");
                return playwright;
            }
    );

    protected static ThreadLocal<Browser> browser = ThreadLocal.withInitial(() ->
            playwright.get().chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
                            .setArgs(Arrays.asList(
                                    "--no-sandbox",
                                    "--disable-blink-features=AutomationControlled",
                                    "--disable-extensions",
                                    "--disable-gpu",
                                    "--start-maximized"
                            ))
            )
    );

    protected BrowserContext browserContext;

    protected Page page;

    @BeforeEach
    void setUpBrowserContext() {
//        boolean isCI = System.getenv("CI") != null;
//        if (isCI) {
//            browserContext = browser.get().newContext(
//                    new Browser.NewContextOptions().setViewportSize(1920, 1080)
//            );
//        } else {
//            browserContext = browser.get().newContext(
//                    new Browser.NewContextOptions().setViewportSize(null)
//            );
//        }
//
//        page = browserContext.newPage();
        boolean isCI = System.getenv("CI") != null;

        Browser.NewContextOptions options = new Browser.NewContextOptions()
                // ДОБАВИТЬ ЭТО: реальный User-Agent, чтобы не выглядеть как Headless-бот
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        if (isCI) {
            options.setViewportSize(1920, 1080);
        } else {
            options.setViewportSize(null);
        }

        browserContext = browser.get().newContext(options);
        page = browserContext.newPage();

        page.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
    }

    @AfterEach
    void closeContext() {
        ScreenshotManager.takeScreenshot(page, "After test");
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.get().close();
        browser.remove();

        playwright.get().close();
        playwright.remove();
    }
}
