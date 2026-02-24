package listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import io.qameta.allure.Attachment;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import reporting.ExtentReportManager;

public class FrameworkTestListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(FrameworkTestListener.class);
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.getInstance();
        LOG.info("Starting suite: {}", context.getSuite().getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        START_TIME.set(System.currentTimeMillis());
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.startTest(testName);
        ExtentReportManager.getTest().info("Test started");
        LOG.info("Test started: {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = testDurationMs();
        ensureExtentTest(result).pass("Test passed in " + duration + " ms");
        LOG.info("Test passed: {} ({} ms)", result.getMethod().getMethodName(), duration);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = testDurationMs();
        WebDriver driver = extractDriver(result);
        String screenshotPath = "";

        if (driver != null) {
            screenshotPath = saveScreenshot(driver, result.getMethod().getMethodName());
            attachAllureScreenshot(driver);
            attachAllureText("Failure URL", safeCurrentUrl(driver));
        }

        Throwable throwable = result.getThrowable();
        String errorMessage = throwable == null ? "Unknown failure" : throwable.toString();

        if (!screenshotPath.isBlank()) {
            ensureExtentTest(result).fail(errorMessage,
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        } else {
            ensureExtentTest(result).fail(errorMessage);
        }

        ensureExtentTest(result).info("Failed in " + duration + " ms");
        attachAllureText("Failure Details", errorMessage);
        LOG.error("Test failed: {} ({} ms)", result.getMethod().getMethodName(), duration, throwable);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ensureExtentTest(result).skip("Test skipped: " + result.getThrowable());
        LOG.warn("Test skipped: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        writeSummary(context);
        ExtentReportManager.flush();
        LOG.info("Finished suite: {}", context.getSuite().getName());
    }

    private long testDurationMs() {
        Long start = START_TIME.get();
        return start == null ? 0 : (System.currentTimeMillis() - start);
    }

    private com.aventstack.extentreports.ExtentTest ensureExtentTest(ITestResult result) {
        if (ExtentReportManager.getTest() == null) {
            ExtentReportManager.startTest(result.getMethod().getMethodName());
        }
        return ExtentReportManager.getTest();
    }

    private WebDriver extractDriver(ITestResult result) {
        Object instance = result.getInstance();
        if (instance == null) {
            return null;
        }
        try {
            Field field = instance.getClass().getSuperclass().getDeclaredField("driver");
            field.setAccessible(true);
            return (WebDriver) field.get(instance);
        } catch (Exception e) {
            return null;
        }
    }

    private String saveScreenshot(WebDriver driver, String testName) {
        try {
            Path screenshotDir = Paths.get("target", "reports", "screenshots");
            Files.createDirectories(screenshotDir);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Path screenshotPath = screenshotDir.resolve(testName + "_" + timestamp + ".png");
            byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(screenshotPath, bytes);
            return screenshotPath.toString();
        } catch (Exception e) {
            LOG.warn("Unable to save screenshot for {}", testName, e);
            return "";
        }
    }

    private void writeSummary(ITestContext context) {
        try {
            int passed = context.getPassedTests().size();
            int failed = context.getFailedTests().size();
            int skipped = context.getSkippedTests().size();
            int total = passed + failed + skipped;

            long durationMs = context.getEndDate().getTime() - context.getStartDate().getTime();
            String summary = "Suite: " + context.getSuite().getName() + System.lineSeparator()
                    + "Total: " + total + System.lineSeparator()
                    + "Passed: " + passed + System.lineSeparator()
                    + "Failed: " + failed + System.lineSeparator()
                    + "Skipped: " + skipped + System.lineSeparator()
                    + "Duration: " + Duration.ofMillis(durationMs) + System.lineSeparator();

            Path reportDir = Paths.get("target", "reports");
            Files.createDirectories(reportDir);
            Files.write(reportDir.resolve("execution-summary.txt"), summary.getBytes(StandardCharsets.UTF_8));
            attachAllureText("Execution Summary", summary);
        } catch (IOException e) {
            LOG.warn("Unable to write execution summary", e);
        }
    }

    private String safeCurrentUrl(WebDriver driver) {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return "N/A";
        }
    }

    @Attachment(value = "Failure Screenshot", type = "image/png")
    public byte[] attachAllureScreenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "{name}", type = "text/plain")
    public String attachAllureText(String name, String value) {
        return value;
    }
}
