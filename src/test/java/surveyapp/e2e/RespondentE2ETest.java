package surveyapp.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import surveyapp.e2e.pages.HomePage;
import surveyapp.e2e.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.*;

class RespondentE2ETest {
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
    void testRespondentRegistersAndLogins() {
        HomePage homePage = new HomePage(page);
        LoginPage loginPage = new LoginPage(page);

        // Home page
        homePage.navigate();
        homePage.clickRegisterButton();

        // Register
        page.fill("input[name='username']", "testuserunique_" + System.currentTimeMillis());
        page.fill("input[name='password']", "testpassword");
        page.click("button:has-text('Registrieren')");

        // Should redirect to home and be logged in
        page.waitForURL("**/");
        assertTrue(homePage.isLoggedIn());
    }

    @Test
    void testRespondentCannotLoginWithWrongPassword() {
        LoginPage loginPage = new LoginPage(page);

        loginPage.navigate();
        loginPage.enterUsername("coordinator");
        loginPage.enterPassword("wrongpassword");
        loginPage.clickLoginButton();

        // Should show error
        assertTrue(loginPage.hasErrorMessage());
        assertTrue(page.url().contains("/login"));
    }
}

