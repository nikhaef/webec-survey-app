package surveyapp.e2e.pages;

import com.microsoft.playwright.Page;

public class LoginPage {
    private Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public void navigate() {
        page.navigate("http://localhost:8081/login");
    }

    public void enterUsername(String username) {
        page.fill("input[name='username']", username);
    }

    public void enterPassword(String password) {
        page.fill("input[name='password']", password);
    }

    public void clickLoginButton() {
        page.click("button:has-text('Login')");
    }

    public boolean hasErrorMessage() {
        return page.textContent("body").contains("Ungültiger Benutzername");
    }

    public String getErrorMessage() {
        return page.textContent(".error");
    }
}

