package com.serenitydojo.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class PlaywrightLocatorsTest {
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
        // browserContext = browser.newContext();
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

    @DisplayName("Locating elements by text")
    @Nested
    class LocatingElementsByTest {

        @BeforeEach
        void openTheCatalogPage() {
            openPage();
        }

        @DisplayName("Locating an element by text contents")
        @Test
        void byTest() {
            page.getByText("Bolt Cutters").click();
            PlaywrightAssertions.assertThat(page.getByText("MightyCraft Hardware")).isVisible();
        }

        @DisplayName("Using alt text")
        @Test
        void byAltTest() {
            page.getByAltText("Combination Pliers").click();
            PlaywrightAssertions.assertThat(page.getByText("ForgeFlex Tools")).isVisible();
        }

        @DisplayName("Using title")
        @Test
        void byTitle() {
            page.getByAltText("Combination Pliers").click();

            page.getByTitle("Practice Software Testing - Toolshop").click();
        }
    }

    @DisplayName("Locating elements by placeholders and labels")
    @Nested
    class LocatingElementsByPlaceholdersAndLabels {

        @BeforeEach
        void openTheContactPage() {
            openPage();
            page.getByText("Contact").click();
        }

        @DisplayName("Using a label")
        @Test
        void byLabel() {
            page.getByLabel("First name").fill("Obi-Wan");
        }

        @DisplayName("Using a placeholder text")
        @Test
        void byPlaceholder() {
            page.getByPlaceholder("Your first name *").fill("Kenobi");
        }
    }

    @DisplayName("Locating elements by role")
    @Nested
    class LocatingElementsByRole {

        @BeforeEach
        void openTheCatalogPage() {
            openPage();
        }

        @DisplayName("Using the BUTTON role")
        @Test
        void byButton() {
            page.getByPlaceholder("Search").fill("Pliers");
            page.getByRole(AriaRole.BUTTON,
                            new Page.GetByRoleOptions()
                                    .setName("Search"))
                    .click();
            Locator cards = page.locator(".card");

            PlaywrightAssertions.assertThat(cards).hasCount(4);
        }

        @DisplayName("Using the HEADING role")
        @Test
        void byHeaderRole() {
            page.getByRole(AriaRole.HEADING,
                            new Page.GetByRoleOptions()
                                    .setName("Combination Pliers"))
                    .click();
            PlaywrightAssertions.assertThat(page.getByText("ForgeFlex Tools")).isVisible();
        }

        @DisplayName("Using the HEADING role and level")
        @Test
        void byHeaderRoleLevel() {
            page.getByRole(AriaRole.HEADING,
                            new Page.GetByRoleOptions()
                                    .setName("Long Nose Pliers")
                                    .setLevel(5))
                    .click();
            PlaywrightAssertions.assertThat(page.getByText("MightyCraft Hardware")).isVisible();
        }

        @DisplayName("Identifying checkboxes")
        @Test
        void byCheckboxes() {
            page.getByRole(AriaRole.CHECKBOX,
                            new Page.GetByRoleOptions()
                                    .setName("Hammer"))
                    .check();
            Locator cards = page.locator(".card");

            PlaywrightAssertions.assertThat(cards).hasCount(7);
        }
    }

    @DisplayName("Locating elements by test Id")
    @Nested
    class LocatingElementsByTestID {

        @BeforeAll
        static void setTestId() {
            playwright.selectors().setTestIdAttribute("data-test");
        }

        @BeforeEach
        void openTheCatalogPage() {
            openPage();
        }

        @DisplayName("Using a custom data-test field")
        @Test
        void byTestId() {
            page.getByTestId("search-query").fill("Pliers");
            page.getByTestId("search-submit").click();
            Locator cards = page.locator(".card");

            PlaywrightAssertions.assertThat(cards).hasCount(4);
        }

    }

    @DisplayName("Locating elements using CSS")
    @Nested
    class LocatingElementsUsingCSS {

        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("By id")
        @Test
        void locateTheFirstNameFieldByID() {
            page.locator("#first_name").fill("Sara-Jane");
            PlaywrightAssertions.assertThat(page.locator("#first_name")).hasValue("Sara-Jane");
        }

        @DisplayName("By CSS class")
        @Test
        void locateTheSendButtonByCssClass() {
            page.locator("#first_name").fill("Sara-Jane");
            page.locator(".btnSubmit").click();
            Locator alerts = page.locator(".alert");
            PlaywrightAssertions.assertThat(alerts).containsText(new String[]{
                    " Last name is required ",
                    " Email is required ",
                    " Subject is required ",
                    " Message is required ",
            });
        }

        @DisplayName("By attribute")
        @Test
        void locateTheSendButtonByAttribute() {
            page.locator("input[placeholder='Your last name *']").fill("Smith");
            PlaywrightAssertions.assertThat(page.locator("#last_name")).hasValue("Smith");
        }
    }

    @DisplayName("Locating elements by text using CSS")
    @Nested
    class LocatingElementsByTextUsingCSS {

        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/contact");
            playwright.selectors().setTestIdAttribute("data-test");
        }

        // :has-text matches any element containing specified text somewhere inside.
        @DisplayName("Using :has-text")
        @Test
        void locateTheSendButtonByText() {
            page.locator("#first_name").fill("Sara-Jane");
            page.locator("input:has-text('Send')").click();
            Locator alerts = page.locator(".alert");
            PlaywrightAssertions.assertThat(alerts).containsText(new String[]{
                    " Last name is required ",
                    " Email is required ",
                    " Subject is required ",
                    " Message is required ",
            });
        }

        // :text matches the smallest element containing specified text.
        @DisplayName("Using :text")
        @Test
        void locateAProductItemByText() {
            page.locator(".navbar :text('Home')").click();
            page.locator(".card :text('Bolt')").click();
            PlaywrightAssertions.assertThat(page.getByTestId("product-name")).hasText("Bolt Cutters");
        }

        // Exact matches
        @DisplayName("Using :text-is")
        @Test
        void locateAProductItemByTextIs() {
            page.locator(".navbar :text('Home')").click();
            page.locator(".card :text-is('Bolt Cutters')").click();
            PlaywrightAssertions.assertThat(page.getByTestId("product-name")).hasText("Bolt Cutters");
        }

        // matching with regular expressions
        @DisplayName("Using :text-matches")
        @Test
        void locateAProductItemByTextMatches() {
            page.locator(".navbar :text('Home')").click();
            page.locator(".card :text-matches('Bolt \\\\w+')").click();
            PlaywrightAssertions.assertThat(page.getByTestId("product-name")).hasText("Bolt Cutters");
        }
    }

    @DisplayName("Locating visible elements")
    @Nested
    class LocatingVisibleElements {
        @BeforeEach
        void openContactPage() {
            openPage();
        }

        @DisplayName("Finding visible and invisible elements")
        @Test
        void locateVisibleAndInvisibleItems() {
            Locator dropdownItems = page.locator(".dropdown-item");

            PlaywrightAssertions.assertThat(dropdownItems).hasCount(11);
        }

        @DisplayName("Finding only visible elements")
        @Test
        void locateVisibleItems() {
            Locator visibleDropdownItems = page.locator(".dropdown-item:visible");

            PlaywrightAssertions.assertThat(visibleDropdownItems)
                    .hasCount(0);
        }
    }

    @DisplayName("Nested locators")
    @Nested
    class NestedLocators {

        @BeforeAll
        static void setTestId() {
            playwright.selectors().setTestIdAttribute("data-test");
        }

        @BeforeEach
        void openTheCatalogPage() {
            openPage();
        }

        @DisplayName("Using roles")
        @Test
        void locatingAMenuItemUsingRoles() {
            page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName("Main menu"))
                    .getByRole(AriaRole.MENUITEM, new Locator.GetByRoleOptions().setName("Contact"))
                    .click();
            PlaywrightAssertions.assertThat(page.getByLabel("First name")).isVisible();
        }

        @DisplayName("Using roles with other strategies")
        @Test
        void locatingAMenuItemUsingRolesAndOtherStrategies() {
            page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName("Main menu"))
                    .getByText("Sign in")
                    .click();
            PlaywrightAssertions.assertThat(page.getByLabel("Email address *")).isVisible();
        }

        @DisplayName("filtering locators by text")
        @Test
        void filteringMenuItems() {
            Locator menuNames = page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName("Main menu"))
                    .getByRole(AriaRole.MENUITEM)
                    .filter(new Locator.FilterOptions().setHasText("Home"));

            PlaywrightAssertions.assertThat(menuNames).hasText(new String[]{
                    " Home "
            });
        }

        @DisplayName("filtering locators by locator")
        @Test
        void filteringMenuItemsByLocator() {
            Locator menuNames = page.getByRole(AriaRole.MENUBAR, new Page.GetByRoleOptions().setName("Main menu"))
                    .getByRole(AriaRole.MENUITEM)
                    .filter(new Locator.FilterOptions().setHas(page.getByText("Contact")));

            PlaywrightAssertions.assertThat(menuNames).hasText(new String[]{
                    " Contact "
            });
        }
    }

    private void openPage() {
        page.navigate("https://practicesoftwaretesting.com/");
    }
}
