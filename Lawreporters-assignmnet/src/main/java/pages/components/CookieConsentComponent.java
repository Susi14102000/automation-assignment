package pages.components;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CookieConsentComponent {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By BANNER_TEXT = By.xpath("//*[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'this website uses cookies')]");
    private static final By ACCEPT_BUTTON = By.xpath("//button[normalize-space()='Accept'] | //a[normalize-space()='Accept']");
    private static final By REJECT_BUTTON = By.xpath("//button[normalize-space()='Reject'] | //a[normalize-space()='Reject']");

    public CookieConsentComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isBannerVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(BANNER_TEXT)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean areAcceptAndRejectVisible() {
        return isElementVisible(ACCEPT_BUTTON) && isElementVisible(REJECT_BUTTON);
    }

    public void clickAccept() {
        clickElement(ACCEPT_BUTTON);
    }

    public void clickReject() {
        clickElement(REJECT_BUTTON);
    }

    public boolean isBannerHidden() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(BANNER_TEXT));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void refreshPage() {
        driver.navigate().refresh();
    }

    public void clearAllCookiesAndStorage() {
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
    }

    public Map<String, String> captureConsentState() {
        Map<String, String> state = new HashMap<>();

        String cookieString = String.valueOf(((JavascriptExecutor) driver).executeScript("return document.cookie || '';"));
        state.put("cookies", cookieString);

        String localStorageSnapshot = String.valueOf(((JavascriptExecutor) driver).executeScript(
                "var out=[]; for (var i=0;i<localStorage.length;i++){ var k=localStorage.key(i); if(k && k.toLowerCase().includes('cookie')){ out.push(k+'='+localStorage.getItem(k)); }} return out.join(';');"));
        state.put("localStorage", localStorageSnapshot);

        String sessionStorageSnapshot = String.valueOf(((JavascriptExecutor) driver).executeScript(
                "var out=[]; for (var i=0;i<sessionStorage.length;i++){ var k=sessionStorage.key(i); if(k && k.toLowerCase().includes('cookie')){ out.push(k+'='+sessionStorage.getItem(k)); }} return out.join(';');"));
        state.put("sessionStorage", sessionStorageSnapshot);

        return state;
    }

    public boolean hasAnyPreferenceStored(Map<String, String> beforeState, Map<String, String> afterState) {
        String beforeCombined = combineState(beforeState);
        String afterCombined = combineState(afterState);
        return !afterCombined.isBlank() && !afterCombined.equals(beforeCombined);
    }

    private boolean isElementVisible(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
    }

    private void clickElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private String combineState(Map<String, String> state) {
        return state.getOrDefault("cookies", "")
                + "|" + state.getOrDefault("localStorage", "")
                + "|" + state.getOrDefault("sessionStorage", "");
    }
}
