package ui;

import base.BaseTest;
import java.util.Arrays;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.Homepage;

public class HomePageTest extends BaseTest {

    private static final List<String> EXPECTED_MENU_ITEMS = Arrays.asList(
            "Home", "Sectors", "Find Lawyer", "Jobs", "Events", "Contact Us");

    @Test(priority = 1, description = "Verify homepage loads successfully")
    public void verifyHomePageLoadsSuccessfully() {
        Homepage homepage = new Homepage(driver);
        homepage.waitForHomePageToLoad();
        Assert.assertTrue(homepage.isHomePageLoaded(), "Homepage did not load correctly");
    }

    @Test(priority = 2, description = "Validate main navigation menu items")
    public void validateMainNavigationMenuItems() {
        Homepage homepage = new Homepage(driver);
        homepage.waitForHomePageToLoad();

        List<String> missingItems = homepage.getMissingNavigationItems(EXPECTED_MENU_ITEMS);
        Assert.assertTrue(missingItems.isEmpty(), "Missing navigation items: " + missingItems);
    }

    @Test(priority = 3, description = "Check that the logo is displayed and clickable")
    public void verifyLogoDisplayedAndClickable() {
        Homepage homepage = new Homepage(driver);
        homepage.waitForHomePageToLoad();
        Assert.assertTrue(homepage.isLogoDisplayed(), "Logo is not displayed");
        Assert.assertTrue(homepage.isLogoClickableAndNavigatesHome(), "Logo is not clickable or did not navigate home");
    }

    @Test(priority = 4, description = "Verify Breaking News section is present")
    public void verifyBreakingNewsSectionPresent() {
        Homepage homepage = new Homepage(driver);
        homepage.waitForHomePageToLoad();
        Assert.assertTrue(homepage.isBreakingNewsSectionPresent(), "Breaking News section is not present");
    }

    @Test(priority = 5, description = "Test newsletter subscription form elements are visible")
    public void verifyNewsletterSubscriptionFormElementsVisible() {
        Homepage homepage = new Homepage(driver);
        homepage.waitForHomePageToLoad();
        Assert.assertTrue(homepage.isNewsletterFormVisible(), "Newsletter form elements are not fully visible");
    }
}
