# Law Reporters Automation Framework

## 1. Objective
This project is a starter automation framework for:
- UI automation (Selenium + TestNG)
- API testing (RestAssured + TestNG)

Target site:
- `https://thelawreporters.com/`

## 2. Project Structure
- `src/main/java/base/DriverFactory.java`  
  Browser driver creation and teardown helpers.
- `src/main/java/pages/`  
  Page Object classes.
- `src/main/java/pages/components/`  
  Reusable page components (example: cookie consent banner).
- `src/main/java/utils/ConfigManager.java`  
  Reads values from `config.properties` and system properties.
- `src/test/java/base/BaseTest.java`  
  Common setup/teardown for UI tests.
- `src/test/java/ui/`  
  UI test classes.
- `src/test/java/ui/responsive/`  
  Responsive design tests for viewport-based behavior.
- `src/test/java/ui/crossbrowser/`  
  Cross-browser critical scenario tests.
- `src/test/java/listeners/`  
  TestNG listeners (reporting, screenshots, execution summary).
- `src/test/java/reporting/`  
  Extent report manager utilities.
- `src/test/java/api/`  
  API test classes.
- `src/test/resources/config.properties`  
  Runtime config (base URL, browser, headless, waits).
- `src/test/resources/simplelogger.properties`  
  Detailed logging configuration for debugging.
- `testsuites/testng.xml`  
  TestNG suite file used by Maven Surefire.
- `testsuites/cross-browser-testng.xml`  
  Dedicated TestNG suite for Chrome/Firefox/Safari critical runs.

## 3. Prerequisites
1. Java 17 installed
2. Maven installed
3. Internet access for first-time dependency download

## 4. Configuration
Edit:
- `src/test/resources/config.properties`

Current defaults:
- `base.url=https://thelawreporters.com/`
- `api.base.url=https://thelawreporters.com/`
- `browser=chrome`
- `headless=true`
- `implicit.wait.seconds=10`

## 5. How Tests Run (Step-by-Step)
1. Run `mvn clean test`
2. Maven Surefire reads `testsuites/testng.xml`
3. TestNG runs classes listed in suite:
   - `ui.HomePageTest`
   - `ui.CookieConsentTest`
   - `ui.NavigationTest`
   - `ui.NewsletterSubscriptionTest`
   - `ui.ContactFormTest`
   - `ui.responsive.ResponsiveDesignTest`
   - `api.HomePageApiTest`
4. For UI test:
   - `BaseTest` starts browser via `DriverFactory`
   - Opens `base.url`
   - Executes test steps in page object/test class
   - Quits browser after test
5. API test sends request to `api.base.url` and validates response.

## 6. Task Coverage Mapping
1. Task 2.2: Homepage Automation
   - Implemented in `ui.HomePageTest` using `pages.Homepage`
   - Scenarios covered:
     - Homepage loads successfully
     - Main navigation items validation
     - Logo displayed and clickable
     - Breaking News section presence
     - Newsletter subscription form visibility
2. Task 2.3: Cookie Consent Testing
   - Implemented in `ui.CookieConsentTest` using `pages.components.CookieConsentComponent`
   - Scenarios covered:
     - Cookie consent modal appears on first visit
     - Accept and Reject button functionality
     - Cookie preference storage validation
     - Modal persistence behavior after accept/reject
3. Task 2.4: Navigation Testing
   - Implemented in `ui.NavigationTest` using `pages.components.NavigationMenuComponent`
   - Scenarios covered:
     - All navigation menu links are functional
     - Jobs menu navigates to `/jobs`
     - Events menu navigates to `/events`
     - Contact Us menu navigates to `/contact-us`
     - Page title validation after navigation
4. Task 3.1: Newsletter Subscription Form
   - Implemented in `ui.NewsletterSubscriptionTest` using `pages.components.NewsletterComponent`
   - Scenarios covered:
     - Valid email format submission
     - Invalid email formats (missing `@`, missing domain, etc.)
     - Empty field submission
     - Success/error feedback message verification
     - Terms acceptance checkbox behavior
5. Task 3.2: Contact Form Testing
   - Implemented in `ui.ContactFormTest` using `pages.components.ContactFormComponent`
   - Scenarios covered:
     - Required field validations
     - Email format validation
     - Phone number validation (if phone field is present)
     - Message field character limit validation
6. Task 4.1: Responsive Design Testing
   - Implemented in `ui.responsive.ResponsiveDesignTest` using `pages.components.ResponsiveLayoutComponent`
   - Scenarios covered:
     - Desktop viewport validation (`1920x1080`)
     - Tablet viewport validation (`768x1024`)
     - Mobile viewport validation (`375x667`)
     - Mobile menu functionality validation
     - Element visibility and layout validation across breakpoints
7. Task 4.3: Cross-Browser Testing
   - Implemented in `ui.crossbrowser.CrossBrowserCriticalTest`
   - Browsers configured:
     - Chrome
     - Firefox
     - Safari (skipped gracefully if unavailable)
   - Critical scenarios executed across browsers:
     - Homepage loads successfully
     - Jobs navigation works
     - Newsletter form is visible
8. Task 5.1: Test Reporting
   - Implemented using TestNG listener: `listeners.FrameworkTestListener`
   - Reporting features:
     - Extent HTML report generation
     - Allure result attachments
     - Screenshot capture on test failure
     - Execution time and pass/fail/skip summary
     - File-based debug logs for test lifecycle and failures

## 7. Current Implemented Tests
1. UI: `ui.HomePageTest`
   - Verifies homepage loads successfully
   - Validates navigation menu items: `Home`, `Sectors`, `Find Lawyer`, `Jobs`, `Events`, `Contact Us`
   - Verifies logo is displayed and clickable
   - Verifies `Breaking News` section is present
   - Verifies newsletter subscription form elements are visible
2. UI: `ui.CookieConsentTest`
   - Verifies cookie consent modal appears on first visit
   - Verifies `Accept` button closes modal and modal does not reappear after refresh
   - Verifies `Reject` button closes modal and modal does not reappear after refresh
   - Verifies cookie preference state is stored after user decision
3. UI: `ui.NavigationTest`
   - Verifies all main menu links return valid HTTP responses
   - Verifies `Jobs` click navigates to `/jobs`
   - Verifies `Events` click navigates to `/events`
   - Verifies `Contact Us` click navigates to `/contact-us`
   - Verifies page title correctness after navigation
4. UI: `ui.NewsletterSubscriptionTest`
   - Verifies valid email format behavior
   - Verifies invalid email format validations using TestNG data provider
   - Verifies empty email submission validation
   - Verifies success/error feedback visibility after submit
   - Verifies terms checkbox participation in submission flow
5. UI: `ui.ContactFormTest`
   - Verifies required field validations on `/contact-us`
   - Verifies invalid email format validation
   - Verifies phone validation when field is present
   - Verifies message field character limit behavior
6. UI: `ui.responsive.ResponsiveDesignTest`
   - Verifies desktop/tablet/mobile viewport layouts
   - Verifies mobile menu toggle/open behavior
   - Verifies key element visibility and no horizontal overflow across breakpoints
7. UI: `ui.crossbrowser.CrossBrowserCriticalTest`
   - Runs 3 critical scenarios for cross-browser validation
8. API: `api.HomePageApiTest`
   - Calls `GET /`
   - Validates status code (`200/301/302`)
   - Validates response contains `lawreporters`

## 8. Useful Commands
1. Run all tests:
```bash
mvn clean test
```
2. Override browser:
```bash
mvn clean test -Dbrowser=firefox
```
3. Override headless:
```bash
mvn clean test -Dheadless=false
```
4. Run only homepage UI tests:
```bash
mvn clean test -Dtest=ui.HomePageTest
```
5. Run only cookie consent tests:
```bash
mvn clean test -Dtest=ui.CookieConsentTest
```
6. Run only API test:
```bash
mvn clean test -Dtest=api.HomePageApiTest
```
7. Run only navigation tests:
```bash
mvn clean test -Dtest=ui.NavigationTest
```
8. Run only newsletter tests:
```bash
mvn clean test -Dtest=ui.NewsletterSubscriptionTest
```
9. Run only contact form tests:
```bash
mvn clean test -Dtest=ui.ContactFormTest
```
10. Run only responsive tests:
```bash
mvn clean test -Dtest=ui.responsive.ResponsiveDesignTest
```
11. Run cross-browser critical suite:
```bash
mvn clean test -DsuiteXmlFile=testsuites/cross-browser-testng.xml
```
12. Generate Allure report (after execution):
```bash
allure generate target/allure-results --clean -o target/allure-report
```
13. Open Allure report:
```bash
allure open target/allure-report
```

## 9. Report Output Paths
1. Extent HTML report: `target/reports/extent-report.html`
2. Execution summary: `target/reports/execution-summary.txt`
3. Failure screenshots: `target/reports/screenshots/`
4. Framework debug log: `target/reports/framework.log`
5. Allure raw results: `target/allure-results/`
6. Allure HTML report: `target/allure-report/` (after `allure generate`)

## 10. Update Log
- 2026-02-24:
  - Added starter framework for UI + API testing.
  - Added `BaseTest`, `DriverFactory`, `ConfigManager`.
  - Added sample tests `ui.HomePageTest` and `api.HomePageApiTest`.
  - Updated all placeholder URLs to `https://thelawreporters.com/`.
  - Fixed TestNG suite path and class package references.
  - Implemented Task 2.2 homepage automation scenarios in `ui.HomePageTest`.
  - Enhanced `pages.Homepage` with homepage-specific locators and reusable validation methods.
  - Added navigation validation with missing-item reporting for quick failure analysis.
  - Added logo display/click behavior validation.
  - Added `Breaking News` section validation.
  - Added newsletter subscription form visibility validation.
  - Removed unwanted `config/` folder from project root.
  - Implemented Task 2.3 cookie consent tests in `ui.CookieConsentTest`.
  - Added `pages.components.CookieConsentComponent` for reusable cookie-banner actions and assertions.
  - Updated TestNG suite to include `ui.CookieConsentTest`.
  - Removed placeholder UI tests `SearchTest` and `NewsletterTest` to reduce noise.
  - Implemented Task 2.4 navigation tests in `ui.NavigationTest`.
  - Added `pages.components.NavigationMenuComponent` for maintainable navigation actions and URL/status checks.
  - Updated TestNG suite to include `ui.NavigationTest`.
  - Added helper methods with clear abstractions for navigation/path/title verification.
  - Implemented Task 3.1 newsletter validation tests in `ui.NewsletterSubscriptionTest`.
  - Added `pages.components.NewsletterComponent` for maintainable newsletter form actions and assertions.
  - Updated TestNG suite to include `ui.NewsletterSubscriptionTest`.
  - Added data-driven invalid email validation and readable helper methods for form setup.
  - Implemented Task 3.2 contact form validation tests in `ui.ContactFormTest`.
  - Added `pages.components.ContactFormComponent` for reusable contact form actions and validations.
  - Updated TestNG suite to include `ui.ContactFormTest`.
  - Added conditional phone validation flow for pages where phone field is optional/absent.
  - Implemented Task 4.1 responsive design tests in `ui.responsive.ResponsiveDesignTest`.
  - Added `pages.components.ResponsiveLayoutComponent` for reusable viewport/layout/menu validation helpers.
  - Created separate folder `src/test/java/ui/responsive/` for responsive test organization.
  - Updated TestNG suite to include `ui.responsive.ResponsiveDesignTest`.
  - Implemented Task 4.3 cross-browser support for Chrome, Firefox, and Safari.
  - Added `ui.crossbrowser.CrossBrowserCriticalTest` with 3 critical scenarios.
  - Added dedicated suite `testsuites/cross-browser-testng.xml` for multi-browser execution.
  - Updated `DriverFactory` with Safari driver support and `BaseTest` with graceful Safari skip behavior.
  - Updated Maven Surefire config to allow suite override via `-DsuiteXmlFile=...`.
  - Implemented Task 5.1 reporting with `listeners.FrameworkTestListener` and `reporting.ExtentReportManager`.
  - Added automatic screenshot capture on test failures and Allure attachments.
  - Added execution summary file with pass/fail/skip statistics and duration.
  - Added detailed framework logs via `src/test/resources/simplelogger.properties`.
  - Enabled listeners in both default and cross-browser TestNG suites.
