package ui.crossbrowser;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.Homepage;
import pages.components.NavigationMenuComponent;
import pages.components.NewsletterComponent;

public class CrossBrowserCriticalTest extends BaseTest {

    @Test(priority = 1, description = "Critical: homepage loads")
    public void criticalHomePageLoads() {
        Homepage homepage = new Homepage(driver);
        homepage.waitForHomePageToLoad();

        Assert.assertTrue(homepage.isHomePageLoaded(), "Homepage should load successfully");
    }

    @Test(priority = 2, description = "Critical: jobs navigation works")
    public void criticalJobsNavigation() {
        NavigationMenuComponent nav = new NavigationMenuComponent(driver);
        nav.waitUntilMenuReady();
        nav.clickMenu("Jobs");
        nav.waitForPageNavigation("/jobs");

        Assert.assertTrue(nav.isOnPath("/jobs"), "Jobs menu should navigate to /jobs");
        Assert.assertTrue(nav.isTitleContaining("jobs"), "Jobs page title should contain 'jobs'");
    }

    @Test(priority = 3, description = "Critical: newsletter form is visible")
    public void criticalNewsletterVisible() {
        NewsletterComponent newsletter = new NewsletterComponent(driver);
        newsletter.prepareForm();

        Assert.assertTrue(newsletter.isFormVisible(), "Newsletter form should be visible");
    }
}
