package com.serenitydojo.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@UsePlaywright(HeadlessChromeOptions.class)
public class AddingItemsToTheCartTest {

    @DisplayName("Search for pliers")
    @Test
    void searchForPliers(Page page) {
        page.navigate("https://practicesoftwaretesting.com/");
        page.getByPlaceholder("Search").fill("Pliers");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();

        PlaywrightAssertions.assertThat(page.locator(".card")).hasCount(4);

//        PlaywrightAssertions.assertThat(page.getByTestId("product-name")).hasText(new String[]{
//                " Combination Pliers ",
//                " Pliers ",
//                " Long Nose Pliers ",
//                " Slip Joint Pliers "
//        });

        List<String> productsNames = page.getByTestId("product-name").allTextContents();
        Assertions.assertThat(productsNames).allMatch(name -> name.contains("Pliers"));

        Locator outOfStockItem = page.locator(".card")
                .filter(new Locator.FilterOptions().setHasText("Out of stock"))
                .getByTestId("product-name");

        PlaywrightAssertions.assertThat(outOfStockItem).hasCount(1);
        PlaywrightAssertions.assertThat(outOfStockItem).hasText("Long Nose Pliers");
    }
}
