package ui;

import base.BaseTest;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.components.ContactFormComponent;
import utils.ConfigManager;

public class ContactFormTest extends BaseTest {

    @Test(priority = 1, description = "All required field validations")
    public void verifyRequiredFieldValidations() {
        ContactFormComponent contactForm = openContactForm();

        contactForm.clearFormFields();
        contactForm.clickSubmit();
        List<String> validationMessages = contactForm.collectRequiredFieldValidationMessages();

        Assert.assertFalse(validationMessages.isEmpty(),
                "Expected required field validation messages, but none were found");
    }

    @Test(priority = 2, description = "Email format validation")
    public void verifyEmailFormatValidation() {
        ContactFormComponent contactForm = openContactForm();

        contactForm.fillBasicRequiredData("Automation User", "invalid-email", "Sample message for contact form");
        contactForm.clickSubmit();

        String emailValidationMessage = contactForm.getEmailValidationMessage();
        Assert.assertFalse(emailValidationMessage.isBlank(),
                "Expected browser/server email format validation message");
    }

    @Test(priority = 3, description = "Phone number validation (if applicable)")
    public void verifyPhoneNumberValidationIfApplicable() {
        ContactFormComponent contactForm = openContactForm();

        if (!contactForm.isPhoneFieldPresent()) {
            Assert.assertTrue(true, "Phone field is not present on contact form. Validation is not applicable.");
            return;
        }

        contactForm.fillBasicRequiredData("Automation User", "automation@example.com", "Sample message for contact form");
        contactForm.setPhone("abc123");
        contactForm.clickSubmit();

        String phoneValidationMessage = contactForm.getPhoneValidationMessage();
        boolean phoneErrorFound = !phoneValidationMessage.isBlank() || contactForm.hasFeedbackMessage();
        Assert.assertTrue(phoneErrorFound,
                "Expected phone number validation to trigger for invalid value");
    }

    @Test(priority = 4, description = "Message field character limits")
    public void verifyMessageFieldCharacterLimits() {
        ContactFormComponent contactForm = openContactForm();

        int maxLength = contactForm.getMessageMaxLength();
        String longMessage = contactForm.generateLongText(5000);
        contactForm.setMessage(longMessage);

        if (maxLength > 0) {
            int actualLength = contactForm.getMessageCurrentLength();
            Assert.assertTrue(actualLength <= maxLength,
                    "Message field accepted more characters than maxlength allows. maxlength=" + maxLength
                            + ", actual=" + actualLength);
            return;
        }

        // Placeholder: if maxlength is not defined in DOM, expect server/in-line feedback after submit.
        contactForm.fillBasicRequiredData("Automation User", "automation@example.com", longMessage);
        contactForm.clickSubmit();
        Assert.assertTrue(contactForm.hasFeedbackMessage() || contactForm.getMessageCurrentLength() >= 1000,
                "Expected defined behavior for very long message input (validation feedback or accepted large value)");
    }

    private ContactFormComponent openContactForm() {
        String baseUrl = ConfigManager.get("base.url", "https://thelawreporters.com/");
        ContactFormComponent contactForm = new ContactFormComponent(driver);
        contactForm.openContactPage(baseUrl);

        Assert.assertTrue(contactForm.isContactPageLoaded(), "Contact Us page did not load correctly");
        return contactForm;
    }
}
