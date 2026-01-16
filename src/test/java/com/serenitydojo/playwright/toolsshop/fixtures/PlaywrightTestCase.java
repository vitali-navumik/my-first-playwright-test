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
        // browserContext = browser.get().newContext();
        boolean isCI = System.getenv("CI") != null;
        if (isCI) {
            browserContext = browser.get().newContext(
                    new Browser.NewContextOptions().setViewportSize(1920, 1080)
            );
        } else {
            browserContext = browser.get().newContext(
                    new Browser.NewContextOptions().setViewportSize(null)
            );
        }

        page = browserContext.newPage();
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
