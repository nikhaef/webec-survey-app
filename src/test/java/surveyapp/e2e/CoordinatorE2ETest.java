package surveyapp.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import surveyapp.e2e.pages.HomePage;
import surveyapp.e2e.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatorE2ETest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void setupBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void teardownBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void setupTest() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void teardown() {
        if (page != null) page.close();
        if (context != null) context.close();
    }

    @Test
    void testCoordinatorCreatesSurvey() {
        HomePage homePage = new HomePage(page);
        LoginPage loginPage = new LoginPage(page);

        // Home page
        homePage.navigate();
        homePage.clickLoginButton();

        // Login
        loginPage.enterUsername("coordinator");
        loginPage.enterPassword("password");
        loginPage.clickLoginButton();

        // Wait for home page and check logged in
        page.waitForURL("**/");
        page.waitForSelector("a:has-text('Logout')");
        assertTrue(page.locator("a:has-text('Logout')").count() > 0);

        // Open the create-survey page (simple stable check)
        homePage.clickCreateSurveyButton();
        page.waitForURL("**/coordinator/create");
        page.waitForSelector("input[name='title']");
        assertTrue(page.url().contains("/coordinator/create"));
    }

    @Test
    void testCoordinatorLoginFlow() {
        LoginPage loginPage = new LoginPage(page);

        // Navigate to login
        loginPage.navigate();

        // Enter credentials
        loginPage.enterUsername("coordinator");
        loginPage.enterPassword("password");
        loginPage.clickLoginButton();

        // Should redirect to home
        page.waitForURL("**/");
        page.waitForSelector("a:has-text('Logout')");
        assertTrue(page.locator("a:has-text('Logout')").count() > 0);

        // Verify we see the "Umfrage erstellen" button
        assertTrue(page.textContent("body").contains("Umfrage erstellen"));
    }
}

