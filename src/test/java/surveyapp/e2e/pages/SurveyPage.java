package surveyapp.e2e.pages;

import com.microsoft.playwright.Page;

public class SurveyPage {
    private Page page;

    public SurveyPage(Page page) {
        this.page = page;
    }

    public void navigateToCreate() {
        page.navigate("http://localhost:8081/coordinator/create");
    }

    public void enterTitle(String title) {
        page.fill("input[name='title']", title);
    }

    public void enterQuestionText(String text) {
        page.fill("input[placeholder='Frage eingeben...']", text);
    }

    public void clickAddQuestionButton() {
        page.click("button:has-text('Frage hinzufügen')");
    }

    public void clickSaveButton() {
        page.click("button:has-text('Speichern')");
    }

    public void clickOpenSurveyButton() {
        page.click("a:has-text('Öffnen')");
    }

    public void clickTakeSurveyButton() {
        page.click("a:has-text('Teilnehmen')");
    }

    public void selectOption(String optionText) {
        page.click("label:has-text('" + optionText + "') input[type='radio']");
    }

    public void clickSubmitButton() {
        page.click("button:has-text('Absenden')");
    }

    public boolean isOnResultsPage() {
        return page.url().contains("/results");
    }

    public String getResultsTitle() {
        return page.textContent("h1");
    }
}

