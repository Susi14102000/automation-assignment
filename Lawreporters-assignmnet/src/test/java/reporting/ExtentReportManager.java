package reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ExtentReportManager {

    private static final ThreadLocal<ExtentTest> TL_TEST = new ThreadLocal<>();
    private static ExtentReports extent;

    private ExtentReportManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            try {
                Path reportDir = Paths.get("target", "reports");
                Files.createDirectories(reportDir);

                ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDir.resolve("extent-report.html").toString());
                sparkReporter.config().setDocumentTitle("Law Reporters Automation Report");
                sparkReporter.config().setReportName("Execution Summary");

                extent = new ExtentReports();
                extent.attachReporter(sparkReporter);
                extent.setSystemInfo("Project", "Law Reporters Automation");
                extent.setSystemInfo("Framework", "Selenium + TestNG");
            } catch (Exception e) {
                throw new RuntimeException("Unable to initialize Extent report", e);
            }
        }
        return extent;
    }

    public static void startTest(String testName) {
        TL_TEST.set(getInstance().createTest(testName));
    }

    public static ExtentTest getTest() {
        return TL_TEST.get();
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
