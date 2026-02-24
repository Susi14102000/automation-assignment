package base;

import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.ConfigManager;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("") String browser) {
        String requestedBrowser = (browser == null || browser.isBlank())
                ? ConfigManager.get("browser", "chrome")
                : browser;

        try {
            driver = DriverFactory.initDriver(requestedBrowser);
        } catch (RuntimeException e) {
            // Safari is optional in many non-macOS environments; skip gracefully when unavailable.
            if ("safari".equalsIgnoreCase(requestedBrowser)) {
                throw new SkipException("Safari is not available in this environment: " + e.getMessage());
            }
            throw e;
        }

        driver.get(ConfigManager.get("base.url", "https://thelawreporters.com/"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
