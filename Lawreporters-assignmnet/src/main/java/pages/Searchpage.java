package pages;

import org.openqa.selenium.WebDriver;

public class Searchpage {

    private final WebDriver driver;

    public Searchpage(WebDriver driver) {
        this.driver = driver;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
