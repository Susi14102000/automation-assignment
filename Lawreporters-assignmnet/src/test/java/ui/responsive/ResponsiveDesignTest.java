package ui.responsive;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.components.ResponsiveLayoutComponent;

public class ResponsiveDesignTest extends BaseTest {

    private static final int DESKTOP_WIDTH = 1920;
    private static final int DESKTOP_HEIGHT = 1080;
    private static final int TABLET_WIDTH = 768;
    private static final int TABLET_HEIGHT = 1024;
    private static final int MOBILE_WIDTH = 375;
    private static final int MOBILE_HEIGHT = 667;

    @Test(priority = 1, description = "Desktop viewport validation (1920x1080)")
    public void verifyDesktopViewportLayout() {
        ResponsiveLayoutComponent responsive = openWithViewport(DESKTOP_WIDTH, DESKTOP_HEIGHT);

        Assert.assertTrue(responsive.isLogoVisible(), "Logo should be visible on desktop viewport");
        Assert.assertTrue(responsive.isAnyNavigationVisible(), "Navigation should be visible on desktop viewport");
        Assert.assertTrue(responsive.hasNoHorizontalOverflow(), "Desktop layout should not overflow horizontally");
    }

    @Test(priority = 2, description = "Tablet viewport validation (768x1024)")
    public void verifyTabletViewportLayout() {
        ResponsiveLayoutComponent responsive = openWithViewport(TABLET_WIDTH, TABLET_HEIGHT);

        Assert.assertTrue(responsive.isLogoVisible(), "Logo should be visible on tablet viewport");
        Assert.assertTrue(
                responsive.isAnyNavigationVisible() || responsive.isMobileMenuToggleVisible(),
                "Navigation or mobile toggle should be visible on tablet viewport");
        Assert.assertTrue(responsive.hasNoHorizontalOverflow(), "Tablet layout should not overflow horizontally");
    }

    @Test(priority = 3, description = "Mobile viewport validation (375x667)")
    public void verifyMobileViewportLayout() {
        ResponsiveLayoutComponent responsive = openWithViewport(MOBILE_WIDTH, MOBILE_HEIGHT);

        Assert.assertTrue(responsive.isLogoVisible(), "Logo should be visible on mobile viewport");
        Assert.assertTrue(
                responsive.isMobileMenuToggleVisible() || responsive.isAnyNavigationVisible(),
                "Mobile viewport should show menu toggle or visible navigation");
        Assert.assertTrue(responsive.hasNoHorizontalOverflow(), "Mobile layout should not overflow horizontally");
    }

    @Test(priority = 4, description = "Verify mobile menu functionality")
    public void verifyMobileMenuFunctionality() {
        ResponsiveLayoutComponent responsive = openWithViewport(MOBILE_WIDTH, MOBILE_HEIGHT);

        if (responsive.isMobileMenuToggleVisible()) {
            boolean clicked = responsive.openMobileMenuIfAvailable();
            Assert.assertTrue(clicked, "Mobile menu toggle should be clickable");
            Assert.assertTrue(responsive.areMainMenuItemsVisible(),
                    "Main menu items should be visible after opening mobile menu");
            return;
        }

        // Placeholder: if site keeps menu expanded on mobile without toggle, assert visible menu items directly.
        Assert.assertTrue(responsive.areMainMenuItemsVisible(),
                "Mobile menu toggle is absent, so main menu items should already be visible");
    }

    @Test(priority = 5, description = "Check element visibility and layout at breakpoints")
    public void verifyElementsAcrossBreakpoints() {
        assertCoreElementsAtViewport(DESKTOP_WIDTH, DESKTOP_HEIGHT, "Desktop");
        assertCoreElementsAtViewport(TABLET_WIDTH, TABLET_HEIGHT, "Tablet");
        assertCoreElementsAtViewport(MOBILE_WIDTH, MOBILE_HEIGHT, "Mobile");
    }

    private void assertCoreElementsAtViewport(int width, int height, String label) {
        ResponsiveLayoutComponent responsive = openWithViewport(width, height);

        Assert.assertTrue(responsive.isLogoVisible(), label + ": logo should be visible");
        Assert.assertTrue(responsive.isAnyCoreSectionVisible(),
                label + ": at least one core section (Breaking News/Newsletter) should be visible");
        Assert.assertTrue(responsive.hasNoHorizontalOverflow(),
                label + ": layout should not have horizontal overflow");
    }

    private ResponsiveLayoutComponent openWithViewport(int width, int height) {
        ResponsiveLayoutComponent responsive = new ResponsiveLayoutComponent(driver);
        responsive.applyViewport(width, height);
        responsive.refreshAndWait();

        Assert.assertTrue(responsive.isViewportApplied(width, height),
                "Expected viewport to be set to " + width + "x" + height);
        return responsive;
    }
}
