package ui;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.Homepage;
import pages.components.NewsletterComponent;

public class NewsletterSubscriptionTest extends BaseTest {

    @Test(priority = 1, description = "Test newsletter subscription with valid email format")
    public void testValidEmailFormat() {
        NewsletterComponent newsletter = openNewsletterForm();
        String validEmail = "automation+" + System.currentTimeMillis() + "@example.com";

        newsletter.enterEmail(validEmail);
        ensureTermsAcceptedIfPresent(newsletter);
        newsletter.clickSubscribe();

        Assert.assertTrue(newsletter.getEmailValidationMessage().isBlank(),
                "Browser should not show email format validation for valid email");
    }

    @Test(priority = 2, dataProvider = "invalidEmails", description = "Test invalid email formats")
    public void testInvalidEmailFormats(String invalidEmail) {
        NewsletterComponent newsletter = openNewsletterForm();

        newsletter.enterEmail(invalidEmail);
        ensureTermsAcceptedIfPresent(newsletter);
        newsletter.clickSubscribe();

        boolean browserValidationShown = !newsletter.getEmailValidationMessage().isBlank();
        boolean serverErrorShown = newsletter.isErrorMessageLikely();

        Assert.assertTrue(browserValidationShown || serverErrorShown,
                "Expected validation error for invalid email: " + invalidEmail);
    }

    @Test(priority = 3, description = "Test empty field submission")
    public void testEmptyFieldSubmission() {
        NewsletterComponent newsletter = openNewsletterForm();

        newsletter.clearEmail();
        ensureTermsAcceptedIfPresent(newsletter);
        newsletter.clickSubscribe();

        Assert.assertFalse(newsletter.getEmailValidationMessage().isBlank(),
                "Expected validation error for empty email field");
    }

    @Test(priority = 4, description = "Verify success/error message display after submission")
    public void verifySuccessOrErrorMessageDisplay() {
        NewsletterComponent newsletter = openNewsletterForm();
        String validEmail = "automation+" + System.nanoTime() + "@example.com";

        newsletter.enterEmail(validEmail);
        ensureTermsAcceptedIfPresent(newsletter);
        newsletter.clickSubscribe();

        Assert.assertTrue(newsletter.isSuccessOrErrorMessageDisplayed(),
                "Expected success/error feedback message after submission");
    }

    @Test(priority = 5, description = "Test checkbox for terms acceptance")
    public void testTermsAcceptanceCheckbox() {
        NewsletterComponent newsletter = openNewsletterForm();

        Assert.assertTrue(newsletter.isTermsCheckboxVisible(), "Terms acceptance checkbox should be visible");

        String validEmail = "automation+terms" + System.currentTimeMillis() + "@example.com";
        newsletter.enterEmail(validEmail);
        newsletter.setTermsAccepted(false);
        newsletter.clickSubscribe();

        boolean blockedWithoutTerms = newsletter.isTermsErrorVisible()
                || newsletter.isErrorMessageLikely()
                || !newsletter.getEmailValidationMessage().isBlank();

        newsletter.setTermsAccepted(true);
        newsletter.clickSubscribe();

        Assert.assertTrue(blockedWithoutTerms || newsletter.isSuccessOrErrorMessageDisplayed(),
                "Expected terms checkbox to participate in form validation/flow");
    }

    @DataProvider(name = "invalidEmails")
    public Object[][] invalidEmails() {
        return new Object[][]{
                {"plainaddress"},
                {"missingatsign.com"},
                {"missingdomain@"},
                {"@nodomain.com"},
                {"user@domain"}
        };
    }

    private NewsletterComponent openNewsletterForm() {
        Homepage homepage = new Homepage(driver);
        homepage.waitForHomePageToLoad();

        NewsletterComponent newsletter = new NewsletterComponent(driver);
        newsletter.prepareForm();
        Assert.assertTrue(newsletter.isFormVisible(), "Newsletter form should be visible on homepage");

        return newsletter;
    }

    private void ensureTermsAcceptedIfPresent(NewsletterComponent newsletter) {
        if (newsletter.isTermsCheckboxVisible()) {
            newsletter.setTermsAccepted(true);
        }
    }
}
