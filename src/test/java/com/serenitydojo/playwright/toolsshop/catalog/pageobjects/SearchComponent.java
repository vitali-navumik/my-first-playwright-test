package com.serenitydojo.playwright.toolsshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

public class SearchComponent {
    private final Page page;

    public SearchComponent(Page page) {
        this.page = page;
    }

    @Step("Search for keyword")
    public void searchBy(String keyword) {
//        page.waitForResponse("**/products/search?q=" + keyword, () -> {
//            page.getByPlaceholder("Search").fill(keyword);
//            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
//        });
        page.waitForResponse("**/products/search?**", () -> {
            page.getByPlaceholder("Search").fill(keyword);
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
        });
        page.waitForTimeout(250);
    }

    @Step("Clear the search criteria")
    public void clearSearch() {
        page.waitForResponse("**/products**",
                () -> page.getByTestId("search-reset").click()
        );
        page.waitForTimeout(250);
    }

    //https://api.practicesoftwaretesting.com/products?page=0&q=saw&between=price,1,100&by_category=01KF1KWYWVEFY2SJX1SMWPX9NS&is_rental=false
    public void filterBy(String filterName) {
        page.waitForResponse("**/products?**by_category**",
                () -> page.getByLabel(filterName).click()
        );
    }

    //https://api.practicesoftwaretesting.com/products?page=0&sort=name,asc&between=price,1,100&is_rental=false
    public void sortBy(String sortFilter) {
        page.waitForResponse("**/products?**sort**",
                () -> page.getByTestId("sort").selectOption(sortFilter)
        );
    }
}

