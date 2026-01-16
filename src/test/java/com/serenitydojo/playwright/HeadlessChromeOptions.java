package com.serenitydojo.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

import java.util.Arrays;

public class HeadlessChromeOptions implements OptionsFactory {
    @Override
    public Options getOptions() {
        return new Options()
                .setHeadless(true)
                .setLaunchOptions(new BrowserType.LaunchOptions()
                        .setArgs(Arrays.asList(
                                "--no-sandbox",
                                "--disable-gpu",
                                "--disable-extensions"
                        )))
                .setContextOptions(new Browser.NewContextOptions()
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                )
                .setTestIdAttribute("data-test");
    }
}
