package surveyapp.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import surveyapp.e2e.pages.HomePage;
import surveyapp.e2e.pages.LoginPage;
import surveyapp.e2e.pages.SurveyPage;

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

    @BeforeAll
    static void teardownBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @org.junit.jupiter.api.BeforeEach
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
    void testCoordinatorCreatesAndOpensSurvey() {
        HomePage homePage = new HomePage(page);
        LoginPage loginPage = new LoginPage(page);
        SurveyPage surveyPage = new SurveyPage(page);

        // Home page
        homePage.navigate();
        homePage.clickLoginButton();

        // Login
        loginPage.enterUsername("coordinator");
        loginPage.enterPassword("password");
        loginPage.clickLoginButton();

        // Wait for home page and check logged in
        page.waitForURL("**/");
        assertTrue(homePage.isLoggedIn());

        // Create survey
        homePage.clickCreateSurveyButton();
        surveyPage.enterTitle("E2E Test Survey");
        surveyPage.enterQuestionText("Which option?");
        surveyPage.clickSaveButton();

        // Back to home, open survey
        page.waitForURL("**/");
        surveyPage.clickOpenSurveyButton();

        // Verify survey is open
        assertTrue(page.textContent("body").contains("E2E Test Survey"));
    }

    @Test
    void testCoordinatorLoginFlow() {
        LoginPage loginPage = new LoginPage(page);
        HomePage homePage = new HomePage(page);

        // Navigate to login
        loginPage.navigate();

        // Enter credentials
        loginPage.enterUsername("coordinator");
        loginPage.enterPassword("password");
        loginPage.clickLoginButton();

        // Should redirect to home
        page.waitForURL("**/");
        assertTrue(homePage.isLoggedIn());

        // Verify we see the "Umfrage erstellen" button
        assertTrue(page.textContent("body").contains("Umfrage erstellen"));
    }
}

