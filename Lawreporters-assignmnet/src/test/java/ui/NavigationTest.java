package ui;

import base.BaseTest;
import java.util.Arrays;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.components.NavigationMenuComponent;
import utils.ConfigManager;

public class NavigationTest extends BaseTest {

    private static final List<String> MAIN_MENU_ITEMS = Arrays.asList(
            "Home", "Sectors", "Find Lawyer", "Jobs", "Events", "Contact Us");

    @Test(priority = 1, description = "All navigation menu links are functional")
    public void verifyAllNavigationMenuLinksAreFunctional() {
        String baseUrl = ConfigManager.get("base.url", "https://thelawreporters.com/");
        NavigationMenuComponent nav = new NavigationMenuComponent(driver);
        nav.waitUntilMenuReady();

        for (String menuItem : MAIN_MENU_ITEMS) {
            String href = nav.getMenuLinkHref(menuItem);
            Assert.assertNotNull(href, "Href should be available for menu item: " + menuItem);
            Assert.assertFalse(href.isBlank(), "Href should not be blank for menu item: " + menuItem);

            String absoluteUrl = nav.toAbsoluteUrl(baseUrl, href);
            int statusCode = nav.getUrlStatusCode(absoluteUrl);
            Assert.assertTrue(statusCode > 0 && statusCode < 400,
                    "Menu link is not functional for '" + menuItem + "'. URL: " + absoluteUrl + ", status: " + statusCode);
        }
    }

    @Test(priority = 2, description = "Clicking Jobs navigates to /jobs page")
    public void verifyJobsNavigation() {
        verifyNavigation("Jobs", "/jobs");
    }

    @Test(priority = 3, description = "Clicking Events navigates to /events page")
    public void verifyEventsNavigation() {
        verifyNavigation("Events", "/events");
    }

    @Test(priority = 4, description = "Clicking Contact Us navigates to /contact-us page")
    public void verifyContactUsNavigation() {
        verifyNavigation("Contact Us", "/contact-us");
    }

    @Test(priority = 5, description = "Verify correct page titles after navigation")
    public void verifyPageTitlesAfterNavigation() {
        verifyNavigationAndTitle("Jobs", "/jobs", "jobs");
        driver.navigate().back();

        verifyNavigationAndTitle("Events", "/events", "events");
        driver.navigate().back();

        verifyNavigationAndTitle("Contact Us", "/contact-us", "contact");
    }

    private void verifyNavigation(String menuLabel, String expectedPath) {
        NavigationMenuComponent nav = new NavigationMenuComponent(driver);
        nav.waitUntilMenuReady();
        nav.clickMenu(menuLabel);
        nav.waitForPageNavigation(expectedPath);
        Assert.assertTrue(nav.isOnPath(expectedPath), "Expected navigation to: " + expectedPath + " via menu: " + menuLabel);
    }

    private void verifyNavigationAndTitle(String menuLabel, String expectedPath, String expectedTitleKeyword) {
        NavigationMenuComponent nav = new NavigationMenuComponent(driver);
        nav.waitUntilMenuReady();
        nav.clickMenu(menuLabel);
        nav.waitForPageNavigation(expectedPath);

        Assert.assertTrue(nav.isOnPath(expectedPath), "Expected path mismatch for menu: " + menuLabel);
        Assert.assertTrue(nav.isTitleContaining(expectedTitleKeyword),
                "Expected page title to contain '" + expectedTitleKeyword + "' for menu: " + menuLabel);
    }
}
