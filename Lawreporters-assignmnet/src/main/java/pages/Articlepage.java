package pages;

import org.openqa.selenium.WebDriver;

public class Articlepage {

    private final WebDriver driver;

    public Articlepage(WebDriver driver) {
        this.driver = driver;
    }

    public String getPageTitle() {
        return driver.getTitle();
    }
}
