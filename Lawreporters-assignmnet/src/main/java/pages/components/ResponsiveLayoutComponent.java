package pages.components;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ResponsiveLayoutComponent {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By LOGO = By.xpath(
            "(//header//img[contains(translate(@alt,'LOGO','logo'),'logo')])[1]"
                    + " | (//img[contains(translate(@alt,'LOGO','logo'),'logo')])[1]");
    private static final By COOKIE_ACCEPT = By.xpath("//button[normalize-space()='Accept']");
    private static final By MOBILE_MENU_TOGGLE = By.xpath(
            "(//button[contains(@class,'menu')"
                    + " or contains(@class,'toggle')"
                    + " or contains(@class,'navbar-toggler')"
                    + " or contains(translate(@aria-label,'MENU','menu'),'menu')"
                    + " or contains(translate(@aria-expanded,'TRUEFALSE','truefalse'),'false')])[1]");
    private static final By BREAKING_NEWS = By.xpath("//*[normalize-space()='Breaking News']");
    private static final By NEWSLETTER_HEADING = By.xpath("//*[contains(normalize-space(),'Our Weekly Newsletter')]");

    private static final List<String> MAIN_MENU_ITEMS = Arrays.asList(
            "Home", "Sectors", "Find Lawyer", "Jobs", "Events", "Contact Us");

    public ResponsiveLayoutComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void applyViewport(int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
    }

    public void refreshAndWait() {
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
        dismissCookieBannerIfPresent();
    }

    public boolean isViewportApplied(int expectedWidth, int expectedHeight) {
        Dimension actual = driver.manage().window().getSize();
        return actual.getWidth() == expectedWidth && actual.getHeight() == expectedHeight;
    }

    public boolean isLogoVisible() {
        return isVisible(LOGO);
    }

    public boolean isMobileMenuToggleVisible() {
        return isVisible(MOBILE_MENU_TOGGLE);
    }

    public boolean isAnyNavigationVisible() {
        for (String menuItem : MAIN_MENU_ITEMS) {
            if (isVisible(menuItemLocator(menuItem))) {
                return true;
            }
        }
        return false;
    }

    public boolean openMobileMenuIfAvailable() {
        if (!isMobileMenuToggleVisible()) {
            return false;
        }

        try {
            WebElement toggle = wait.until(ExpectedConditions.elementToBeClickable(MOBILE_MENU_TOGGLE));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", toggle);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean areMainMenuItemsVisible() {
        for (String item : MAIN_MENU_ITEMS) {
            if (!isVisible(menuItemLocator(item))) {
                return false;
            }
        }
        return true;
    }

    public boolean hasNoHorizontalOverflow() {
        Object result = ((JavascriptExecutor) driver).executeScript(
                "return document.documentElement.scrollWidth <= (window.innerWidth + 2);");
        return Boolean.TRUE.equals(result);
    }

    public boolean isAnyCoreSectionVisible() {
        return isVisible(BREAKING_NEWS) || isVisible(NEWSLETTER_HEADING);
    }

    private boolean isVisible(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
    }

    private By menuItemLocator(String label) {
        String escaped = label.replace("'", "\\'");
        return By.xpath(
                "(//header//a[normalize-space()='" + escaped + "']"
                        + " | //nav//a[normalize-space()='" + escaped + "']"
                        + " | //a[normalize-space()='" + escaped + "'])[1]");
    }

    private void dismissCookieBannerIfPresent() {
        List<WebElement> accepts = driver.findElements(COOKIE_ACCEPT);
        if (!accepts.isEmpty()) {
            try {
                WebElement accept = accepts.get(0);
                if (accept.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", accept);
                }
            } catch (Exception ignored) {
                // Placeholder: update cookie consent locator handling here if site markup changes.
            }
        }
    }
}
