package pages.components;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NavigationMenuComponent {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final HttpClient httpClient;

    private static final By COOKIE_ACCEPT = By.xpath("//button[normalize-space()='Accept']");

    public NavigationMenuComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public void waitUntilMenuReady() {
        wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
    }

    public String getMenuLinkHref(String menuLabel) {
        dismissCookieBannerIfPresent();
        WebElement menuLink = wait.until(ExpectedConditions.visibilityOfElementLocated(menuLinkBy(menuLabel)));
        return menuLink.getAttribute("href");
    }

    public void clickMenu(String menuLabel) {
        dismissCookieBannerIfPresent();
        WebElement menuLink = wait.until(ExpectedConditions.elementToBeClickable(menuLinkBy(menuLabel)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuLink);
    }

    public boolean isOnPath(String expectedPath) {
        String actualPath = currentPath();
        String normalizedExpected = normalizePath(expectedPath);
        return actualPath.equals(normalizedExpected);
    }

    public boolean isTitleContaining(String titleKeyword) {
        String title = driver.getTitle();
        return title != null && title.toLowerCase().contains(titleKeyword.toLowerCase());
    }

    public int getUrlStatusCode(String absoluteUrl) {
        try {
            // Try HEAD first (faster); if blocked by server, fallback to GET.
            HttpRequest headRequest = HttpRequest.newBuilder()
                    .uri(URI.create(absoluteUrl))
                    .timeout(Duration.ofSeconds(20))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> headResponse = httpClient.send(headRequest, HttpResponse.BodyHandlers.discarding());
            if (headResponse.statusCode() < 400) {
                return headResponse.statusCode();
            }

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(absoluteUrl))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<Void> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.discarding());
            return getResponse.statusCode();
        } catch (Exception e) {
            return 0;
        }
    }

    public String toAbsoluteUrl(String baseUrl, String href) {
        if (href == null || href.isBlank()) {
            return "";
        }
        if (href.startsWith("http://") || href.startsWith("https://")) {
            return href;
        }
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String normalizedHref = href.startsWith("/") ? href : "/" + href;
        return normalizedBase + normalizedHref;
    }

    public void waitForPageNavigation(String expectedPath) {
        String normalizedExpected = normalizePath(expectedPath);
        wait.until(d -> currentPath().equals(normalizedExpected));
    }

    private By menuLinkBy(String label) {
        String escaped = label.replace("'", "\\'");
        // Fallback locator chain for slightly different header/nav structures.
        return By.xpath(
                "(//header//a[normalize-space()='" + escaped + "']"
                        + " | //nav//a[normalize-space()='" + escaped + "']"
                        + " | //a[normalize-space()='" + escaped + "'])[1]");
    }

    private String currentPath() {
        try {
            URI uri = URI.create(driver.getCurrentUrl());
            return normalizePath(uri.getPath());
        } catch (Exception e) {
            return "";
        }
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private void dismissCookieBannerIfPresent() {
        try {
            WebElement accept = wait.until(ExpectedConditions.visibilityOfElementLocated(COOKIE_ACCEPT));
            if (accept.isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", accept);
            }
        } catch (TimeoutException ignored) {
            // Cookie banner might not be present after preference is already stored.
        }
    }
}
