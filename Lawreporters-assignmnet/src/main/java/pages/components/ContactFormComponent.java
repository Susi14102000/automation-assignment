package pages.components;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ContactFormComponent {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By CONTACT_FORM = By.xpath("(//form[.//textarea or .//input])[1]");
    private static final By NAME_FIELD = By.xpath(
            "(//input[contains(translate(@name,'NAME','name'),'name')"
                    + " or contains(translate(@id,'NAME','name'),'name')"
                    + " or contains(translate(@placeholder,'NAME','name'),'name')])[1]");
    private static final By EMAIL_FIELD = By.xpath(
            "(//input[@type='email'"
                    + " or contains(translate(@name,'EMAIL','email'),'email')"
                    + " or contains(translate(@id,'EMAIL','email'),'email')])[1]");
    private static final By PHONE_FIELD = By.xpath(
            "(//input[@type='tel'"
                    + " or contains(translate(@name,'PHONE','phone'),'phone')"
                    + " or contains(translate(@name,'MOBILE','mobile'),'mobile')"
                    + " or contains(translate(@id,'PHONE','phone'),'phone')])[1]");
    private static final By SUBJECT_FIELD = By.xpath(
            "(//input[contains(translate(@name,'SUBJECT','subject'),'subject')"
                    + " or contains(translate(@id,'SUBJECT','subject'),'subject')])[1]");
    private static final By MESSAGE_FIELD = By.xpath(
            "(//textarea[contains(translate(@name,'MESSAGE','message'),'message')"
                    + " or contains(translate(@id,'MESSAGE','message'),'message')"
                    + " or @rows])[1]");
    private static final By SUBMIT_BUTTON = By.xpath(
            "(//button[@type='submit'"
                    + " or contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'send')"
                    + " or contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submit')]"
                    + " | //input[@type='submit'])[1]");

    private static final By INLINE_ERRORS = By.xpath(
            "//*[contains(@class,'error')"
                    + " or contains(@class,'invalid')"
                    + " or contains(@class,'not-valid')"
                    + " or contains(@class,'wpcf7-not-valid-tip')"
                    + "][string-length(normalize-space())>0]");

    public ContactFormComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void openContactPage(String baseUrl) {
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        driver.get(normalizedBase + "/contact-us");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CONTACT_FORM));
        waitForDocumentReady();
    }

    public boolean isContactPageLoaded() {
        return driver.getCurrentUrl().contains("/contact-us")
                && !driver.getTitle().isBlank();
    }

    public void clearFormFields() {
        clearIfPresent(NAME_FIELD);
        clearIfPresent(EMAIL_FIELD);
        clearIfPresent(PHONE_FIELD);
        clearIfPresent(SUBJECT_FIELD);
        clearIfPresent(MESSAGE_FIELD);
    }

    public void fillBasicRequiredData(String name, String email, String message) {
        typeIfPresent(NAME_FIELD, name);
        typeIfPresent(EMAIL_FIELD, email);
        typeIfPresent(MESSAGE_FIELD, message);
    }

    public void setEmail(String email) {
        typeIfPresent(EMAIL_FIELD, email);
    }

    public void setPhone(String phone) {
        typeIfPresent(PHONE_FIELD, phone);
    }

    public void setMessage(String message) {
        typeIfPresent(MESSAGE_FIELD, message);
    }

    public void clickSubmit() {
        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
    }

    public List<String> collectRequiredFieldValidationMessages() {
        List<String> messages = new ArrayList<>();
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(CONTACT_FORM));

        List<WebElement> requiredFields = form.findElements(By.cssSelector("input[required], textarea[required], select[required]"));
        if (requiredFields.isEmpty()) {
            // Placeholder: if page removes `required` attribute, fallback to commonly-required fields.
            addIfPresent(requiredFields, NAME_FIELD);
            addIfPresent(requiredFields, EMAIL_FIELD);
            addIfPresent(requiredFields, MESSAGE_FIELD);
        }

        for (WebElement field : requiredFields) {
            String message = getValidationMessage(field);
            if (!message.isBlank()) {
                messages.add(message);
            }
        }

        if (messages.isEmpty()) {
            List<WebElement> inlineErrors = driver.findElements(INLINE_ERRORS);
            for (WebElement error : inlineErrors) {
                String text = error.getText().trim();
                if (!text.isBlank()) {
                    messages.add(text);
                }
            }
        }

        return messages;
    }

    public String getEmailValidationMessage() {
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_FIELD));
        return getValidationMessage(emailField);
    }

    public boolean isPhoneFieldPresent() {
        return !driver.findElements(PHONE_FIELD).isEmpty();
    }

    public String getPhoneValidationMessage() {
        if (!isPhoneFieldPresent()) {
            return "";
        }
        WebElement phoneField = wait.until(ExpectedConditions.visibilityOfElementLocated(PHONE_FIELD));
        return getValidationMessage(phoneField);
    }

    public int getMessageMaxLength() {
        List<WebElement> fields = driver.findElements(MESSAGE_FIELD);
        if (fields.isEmpty()) {
            return -1;
        }
        String maxLength = fields.get(0).getAttribute("maxlength");
        if (maxLength == null || maxLength.isBlank()) {
            return -1;
        }
        try {
            return Integer.parseInt(maxLength);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int getMessageCurrentLength() {
        List<WebElement> fields = driver.findElements(MESSAGE_FIELD);
        if (fields.isEmpty()) {
            return -1;
        }
        String value = fields.get(0).getAttribute("value");
        return value == null ? 0 : value.length();
    }

    public boolean hasFeedbackMessage() {
        try {
            return !wait.until(ExpectedConditions.visibilityOfElementLocated(INLINE_ERRORS)).getText().trim().isBlank();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String generateLongText(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append('a');
        }
        return builder.toString();
    }

    private void typeIfPresent(By locator, String value) {
        List<WebElement> fields = driver.findElements(locator);
        if (!fields.isEmpty()) {
            WebElement field = wait.until(ExpectedConditions.visibilityOf(fields.get(0)));
            field.clear();
            field.sendKeys(value);
        }
    }

    private void clearIfPresent(By locator) {
        List<WebElement> fields = driver.findElements(locator);
        if (!fields.isEmpty()) {
            fields.get(0).clear();
        }
    }

    private void addIfPresent(List<WebElement> list, By locator) {
        List<WebElement> matches = driver.findElements(locator);
        if (!matches.isEmpty()) {
            list.add(matches.get(0));
        }
    }

    private String getValidationMessage(WebElement field) {
        Object message = ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage || ''", field);
        return String.valueOf(message).trim();
    }

    private void waitForDocumentReady() {
        wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
    }
}
