package com.serenitydojo.playwright.toolsshop.login.pageobjects;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.serenitydojo.playwright.toolsshop.domain.User;
import com.serenitydojo.playwright.toolsshop.fixtures.ScreenshotManager;

public class LoginPage {
    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void open() {
        //  page.navigate("https://practicesoftwaretesting.com/auth/login"); // because open captcha when run from gitHub
        page.navigate("https://practicesoftwaretesting.com");
        page.getByText("Sign in").click();
        ScreenshotManager.takeScreenshot(page, "Login page");
    }

    public void loginAs(User user) {
        page.getByPlaceholder("Your email").fill(user.email());
        page.getByPlaceholder("Your password").fill(user.password());
        // page.getByTestId("login-submit").click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
    }

    public String title() {
        return page.getByTestId("page-title").textContent();
    }

    public String loginErrorMessage() {
        return page.getByTestId("login-error").textContent();
    }
}
