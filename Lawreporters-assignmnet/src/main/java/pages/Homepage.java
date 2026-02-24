package pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Homepage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By HOME_MARKER = By.xpath("//body");
    private static final By BREAKING_NEWS = By.xpath("//*[normalize-space()='Breaking News']");
    private static final By NEWSLETTER_HEADING = By.xpath("//*[contains(normalize-space(),\"Our Weekly Newsletter\")]");
    private static final By NEWSLETTER_SUBSCRIBE_BUTTON = By.xpath(
            "//button[contains(normalize-space(),'Subscribe')]"
                    + " | //input[@type='submit' and contains(@value,'Subscribe')]"
                    + " | //a[contains(normalize-space(),'Subscribe')]");
    private static final By NEWSLETTER_INPUT = By.xpath(
            "//input[@type='email']"
                    + " | //input[contains(translate(@name,'EMAIL','email'),'email')]"
                    + " | //input[contains(translate(@placeholder,'EMAIL','email'),'email')]");
    private static final By COOKIE_ACCEPT = By.xpath("//button[normalize-space()='Accept']");
    private static final By LOGO = By.xpath(
            "(//header//img[contains(translate(@alt,'LOGO','logo'),'logo')])[1]"
                    + " | (//img[contains(translate(@alt,'LOGO','logo'),'logo')])[1]");

    public Homepage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void waitForHomePageToLoad() {
        wait.until(ExpectedConditions.presenceOfElementLocated(HOME_MARKER));
        wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    public boolean isHomePageLoaded() {
        return isTitlePresent() && getCurrentUrl().contains("thelawreporters.com");
    }

    public List<String> getMissingNavigationItems(List<String> expectedMenuItems) {
        List<String> missing = new ArrayList<>();
        for (String item : expectedMenuItems) {
            if (!isHeaderTextVisible(item)) {
                missing.add(item);
            }
        }
        return missing;
    }

    public boolean isLogoDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(LOGO)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isLogoClickableAndNavigatesHome() {
        try {
            dismissCookieBannerIfPresent();
            WebElement logoElement = wait.until(ExpectedConditions.elementToBeClickable(LOGO));
            WebElement linkElement = logoElement.findElement(By.xpath("./ancestor::a[1]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", linkElement);
            waitForHomePageToLoad();
            return getCurrentUrl().contains("thelawreporters.com");
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    public boolean isBreakingNewsSectionPresent() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(BREAKING_NEWS)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isNewsletterFormVisible() {
        boolean headingVisible = isElementVisible(NEWSLETTER_HEADING);
        boolean subscribeVisible = isElementVisible(NEWSLETTER_SUBSCRIBE_BUTTON);
        boolean inputVisible = isElementVisible(NEWSLETTER_INPUT);
        return headingVisible && subscribeVisible && inputVisible;
    }

    public void dismissCookieBannerIfPresent() {
        List<WebElement> buttons = driver.findElements(COOKIE_ACCEPT);
        if (!buttons.isEmpty()) {
            try {
                WebElement accept = buttons.get(0);
                if (accept.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", accept);
                }
            } catch (Exception ignored) {
                // Best-effort cookie dismissal; test should continue if banner is absent/uninteractive.
            }
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isTitlePresent() {
        return driver.getTitle() != null && !driver.getTitle().isBlank();
    }

    private boolean isHeaderTextVisible(String text) {
        String escaped = text.replace("'", "\\'");
        By locator = By.xpath(
                "(//header//*[normalize-space()='" + escaped + "']"
                        + " | //nav//*[normalize-space()='" + escaped + "']"
                        + " | //*[normalize-space()='" + escaped + "'])[1]");
        return isElementVisible(locator);
    }

    private boolean isElementVisible(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }
}
