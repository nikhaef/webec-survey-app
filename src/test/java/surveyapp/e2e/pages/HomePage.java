package surveyapp.e2e.pages;

import com.microsoft.playwright.Page;

public class HomePage {
    private Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    public void navigate() {
        page.navigate("http://localhost:8081/");
    }

    public void clickLoginButton() {
        page.click("a:has-text('Login')");
    }

    public void clickRegisterButton() {
        page.click("a:has-text('Registrieren')");
    }

    public void clickCreateSurveyButton() {
        page.click("a.wide:has-text('Umfrage erstellen')");
    }

    public void clickTakeSurveyButton() {
        page.click("a:has-text('Teilnehmen')");
    }

    public String getCurrentUrl() {
        return page.url();
    }

    public boolean isLoggedIn() {
        return page.textContent("body").contains("Logout");
    }

    public String getUsername() {
        String text = page.textContent("body");
        if (text.contains("Hallo")) {
            return text.substring(text.indexOf("Hallo") + 6, text.indexOf("Logout") - 1).trim();
        }
        return "";
    }
}

