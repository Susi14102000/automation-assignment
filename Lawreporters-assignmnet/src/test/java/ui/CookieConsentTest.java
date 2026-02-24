package ui;

import base.BaseTest;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.Homepage;
import pages.components.CookieConsentComponent;

public class CookieConsentTest extends BaseTest {

    @Test(priority = 1, description = "Cookie consent modal appears on first visit")
    public void verifyCookieModalAppearsOnFirstVisit() {
        CookieConsentComponent cookieConsent = openFreshHomePage();

        Assert.assertTrue(cookieConsent.isBannerVisible(), "Cookie consent modal should appear on first visit");
        Assert.assertTrue(cookieConsent.areAcceptAndRejectVisible(), "Accept and Reject buttons should be visible");
    }

    @Test(priority = 2, description = "Accept button is functional and modal does not reappear")
    public void verifyAcceptButtonFunctionalityAndPersistence() {
        CookieConsentComponent cookieConsent = openFreshHomePage();

        Assert.assertTrue(cookieConsent.isBannerVisible(), "Cookie consent modal should appear before accepting");
        cookieConsent.clickAccept();
        Assert.assertTrue(cookieConsent.isBannerHidden(), "Cookie consent modal should close after Accept");

        cookieConsent.refreshPage();
        Assert.assertFalse(cookieConsent.isBannerVisible(), "Cookie modal should not reappear after accepting");
    }

    @Test(priority = 3, description = "Reject button is functional and modal does not reappear")
    public void verifyRejectButtonFunctionalityAndPersistence() {
        CookieConsentComponent cookieConsent = openFreshHomePage();

        Assert.assertTrue(cookieConsent.isBannerVisible(), "Cookie consent modal should appear before rejecting");
        cookieConsent.clickReject();
        Assert.assertTrue(cookieConsent.isBannerHidden(), "Cookie consent modal should close after Reject");

        cookieConsent.refreshPage();
        Assert.assertFalse(cookieConsent.isBannerVisible(), "Cookie modal should not reappear after rejecting");
    }

    @Test(priority = 4, description = "Cookie preference is stored after decision")
    public void verifyCookiePreferenceStoredCorrectly() {
        CookieConsentComponent cookieConsent = openFreshHomePage();

        Map<String, String> beforeState = cookieConsent.captureConsentState();
        cookieConsent.clickAccept();
        Assert.assertTrue(cookieConsent.isBannerHidden(), "Cookie consent modal should close after Accept");

        Map<String, String> afterState = cookieConsent.captureConsentState();
        Assert.assertTrue(
                cookieConsent.hasAnyPreferenceStored(beforeState, afterState),
                "Cookie/localStorage/sessionStorage state should change after setting preference");
    }

    private CookieConsentComponent openFreshHomePage() {
        Homepage homepage = new Homepage(driver);
        CookieConsentComponent cookieConsent = new CookieConsentComponent(driver);

        cookieConsent.clearAllCookiesAndStorage();
        driver.navigate().refresh();
        homepage.waitForHomePageToLoad();

        return cookieConsent;
    }
}
