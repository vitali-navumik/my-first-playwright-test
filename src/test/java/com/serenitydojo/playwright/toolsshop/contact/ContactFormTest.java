package com.serenitydojo.playwright.toolsshop.contact;

import com.microsoft.playwright.options.AriaRole;
import com.serenitydojo.playwright.toolsshop.catalog.pageobjects.NavBar;
import com.serenitydojo.playwright.toolsshop.fixtures.PlaywrightTestCase;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Contact form")
@Feature("Contacts")
public class ContactFormTest extends PlaywrightTestCase {
    ContactForm contactForm;
    NavBar navBar;

    @BeforeEach
    void openContactPage() {
        contactForm = new ContactForm(page);
        navBar = new NavBar(page);
        navBar.openContactPage();
    }

    @Story("Contact form")
    @DisplayName("Customers can use the contact form to contact us")
    @Test
    void completeForm() throws URISyntaxException {
        contactForm.setFirstName("Sara-Jane");
        contactForm.setLastName("Smith");
        contactForm.setEmail("sara-jane@example.com");
        contactForm.setMessage("Hello, world! This is test message for training lesson!");
        contactForm.selectSubject("Warranty");
        Path fileToUpload = Paths.get(ClassLoader.getSystemResource("data/sample-data.txt").toURI());
        contactForm.setAttachment(fileToUpload);

        assertThat(contactForm.getFirstNameField()).hasValue("Sara-Jane");
        assertThat(contactForm.getLastNameField()).hasValue("Smith");
        assertThat(contactForm.getEmail()).hasValue("sara-jane@example.com");
        assertThat(contactForm.getMessage()).hasValue("Hello, world! This is test message for training lesson!");
        assertThat(contactForm.getSubjectField()).hasValue("warranty");
        String uploadedFile = contactForm.getAttachment().inputValue();
        Assertions.assertThat(uploadedFile).endsWith("sample-data.txt");

        contactForm.submitForm();

        Assertions.assertThat(contactForm.getAlertMessage()).contains("Thanks for your message! We will contact you shortly.");
    }

    @Story("Contact form")
    @DisplayName("First name, last name, email and message are mandatory")
    @ParameterizedTest
    @ValueSource(strings = {"First name", "Last name", "Email", "Message", "Subject"})
    void mandatoryFields(String fieldName) {
        // Fill in the field values
        contactForm.setFirstName("Sara-Jane");
        contactForm.setLastName("Smith");
        contactForm.setEmail("sara-jane@example.com");
        contactForm.setMessage("Hello, world! This is test message for training lesson!");

        if (!fieldName.equals("Subject")) {
            contactForm.selectSubject("Warranty");
        }

        // Clear one of the field
        if (!fieldName.equals("Subject")) {
            contactForm.clearField(fieldName);
            assertThat(page.getByLabel(fieldName)).hasClass(Pattern.compile(".*ng-invalid.*"));
        }

        contactForm.submitForm();

        // Check the error message for that field
        var errorMessage = page.getByRole(AriaRole.ALERT).getByText(fieldName + " is required");

        assertThat(errorMessage).isVisible();
    }

    @Story("Contact form")
    @DisplayName("The message must be at least 50 characters long")
    @Test
    void messageTooShort() {
        contactForm.setFirstName("Sara-Jane");
        contactForm.setLastName("Smith");
        contactForm.setEmail("sara-jane@example.com");
        contactForm.setMessage("A shot message");
        contactForm.selectSubject("Warranty");

        contactForm.submitForm();

        assertThat(page.getByRole(AriaRole.ALERT)).hasText("Message must be minimal 50 characters");
    }

    @Story("Contact form")
    @DisplayName("The email address must be correctly formatted")
    @ParameterizedTest(name = "'{arguments}' should be rejected")
    @ValueSource(strings = {"not-an-email", "not-an.email.com", "notanemail"})
    void invalidEmailField(String invalidEmail) {
        contactForm.setFirstName("Sarah-Jane");
        contactForm.setLastName("Smith");
        contactForm.setEmail(invalidEmail);
        contactForm.setMessage("A very long message to the warranty service about a warranty on a product!");
        contactForm.selectSubject("Warranty");

        contactForm.submitForm();

        assertThat(page.getByRole(AriaRole.ALERT)).hasText("Email format is invalid");
    }
}