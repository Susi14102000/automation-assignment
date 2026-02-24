package pages;

import org.openqa.selenium.WebDriver;

public class ContactPage {

    private final WebDriver driver;

    public ContactPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
