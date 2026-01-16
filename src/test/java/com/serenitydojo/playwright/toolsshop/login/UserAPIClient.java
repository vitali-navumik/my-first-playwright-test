package com.serenitydojo.playwright.toolsshop.login;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.RequestOptions;
import com.serenitydojo.playwright.toolsshop.domain.User;

public class UserAPIClient {
    private final Page page;

    private static final String REGISTER_USER = "https://api.practicesoftwaretesting.com/users/register";

    public UserAPIClient(Page page) {
        this.page = page;
    }

    public void registerUser(User user) {
        var response = page.request().post(
                REGISTER_USER,
                RequestOptions.create()
                        .setData(user)
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Accept", "application/json"));
        if (response.status() != 201) {
            throw new IllegalStateException("Could not create user: " + response.text());
        }
    }
}
