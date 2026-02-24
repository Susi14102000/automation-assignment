package pages;

import org.openqa.selenium.WebDriver;

public class NewsLetterpage {

    private final WebDriver driver;

    public NewsLetterpage(WebDriver driver) {
        this.driver = driver;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
