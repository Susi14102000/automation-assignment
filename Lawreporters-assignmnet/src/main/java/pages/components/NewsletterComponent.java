package pages.components;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NewsletterComponent {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By COOKIE_ACCEPT = By.xpath("//button[normalize-space()='Accept']");
    private static final By EMAIL_INPUT = By.xpath(
            "(//input[@type='email']"
                    + " | //input[contains(translate(@name,'EMAIL','email'),'email')]"
                    + " | //input[contains(translate(@placeholder,'EMAIL','email'),'email')])[1]");
    private static final By SUBSCRIBE_BUTTON = By.xpath(
            "(//button[contains(normalize-space(),'Subscribe')]"
                    + " | //input[@type='submit' and contains(@value,'Subscribe')]"
                    + " | //a[contains(normalize-space(),'Subscribe')])[1]");
    private static final By TERMS_CHECKBOX = By.xpath(
            "(//input[@type='checkbox'][contains(translate(@name,'TERMS','terms'),'term')"
                    + " or contains(translate(@id,'TERMS','terms'),'term')]"
                    + " | //input[@type='checkbox'])[1]");
    private static final By RESPONSE_MESSAGE = By.xpath(
            "(//*[contains(@class,'message') or contains(@class,'alert')"
                    + " or contains(@class,'error') or contains(@class,'success')]"
                    + "[string-length(normalize-space())>0])[1]");
    private static final By TERMS_ERROR = By.xpath(
            "//*[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'terms')"
                    + " and (contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'accept')"
                    + " or contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'required'))]");

    public NewsletterComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    public void prepareForm() {
        dismissCookieBannerIfPresent();
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", emailField);
    }

    public boolean isFormVisible() {
        return isElementVisible(EMAIL_INPUT) && isElementVisible(SUBSCRIBE_BUTTON);
    }

    public void enterEmail(String email) {
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
        emailField.clear();
        emailField.sendKeys(email);
    }

    public void clearEmail() {
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
        emailField.clear();
    }

    public void clickSubscribe() {
        WebElement subscribe = wait.until(ExpectedConditions.elementToBeClickable(SUBSCRIBE_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subscribe);
    }

    public String getEmailValidationMessage() {
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
        Object message = ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage || ''", emailField);
        return String.valueOf(message).trim();
    }

    public boolean isTermsCheckboxVisible() {
        return isElementVisible(TERMS_CHECKBOX);
    }

    public void setTermsAccepted(boolean accepted) {
        WebElement checkbox = wait.until(ExpectedConditions.elementToBeClickable(TERMS_CHECKBOX));
        if (checkbox.isSelected() != accepted) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
        }
    }

    public boolean isTermsErrorVisible() {
        return isElementVisible(TERMS_ERROR);
    }

    public String getResponseMessage() {
        try {
            WebElement response = wait.until(ExpectedConditions.visibilityOfElementLocated(RESPONSE_MESSAGE));
            return response.getText().trim();
        } catch (TimeoutException e) {
            return "";
        }
    }

    public boolean isSuccessOrErrorMessageDisplayed() {
        String response = getResponseMessage();
        if (!response.isBlank()) {
            return true;
        }
        String validation = getEmailValidationMessage();
        return !validation.isBlank();
    }

    public boolean isErrorMessageLikely() {
        String text = getResponseMessage().toLowerCase();
        return text.contains("error")
                || text.contains("invalid")
                || text.contains("required")
                || text.contains("already")
                || text.contains("not valid");
    }

    private boolean isElementVisible(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
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
                // Placeholder: if cookie banner implementation changes, only this locator/method needs update.
            }
        }
    }
}
