package surveyapp.e2e.pages;

import com.microsoft.playwright.Page;

public class SurveyPage {
    private final Page page;

    public SurveyPage(Page page) {
        this.page = page;
    }

    public void enterTitle(String title) {
        page.waitForSelector("input[name='title']");
        page.fill("input[name='title']", title);
    }

    public void enterQuestionText(String text) {
        page.waitForSelector("input[placeholder='Frage eingeben...']");
        page.fill("input[placeholder='Frage eingeben...']", text);
    }

    public void enterOptionText(int optionIndex, String text) {
        page.waitForSelector("input[name='q0opt" + optionIndex + "']");
        page.fill("input[name='q0opt" + optionIndex + "']", text);
    }

    public void clickSaveButton() {
        page.waitForSelector("button:has-text('Speichern')");
        page.click("button:has-text('Speichern')", new Page.ClickOptions().setNoWaitAfter(true));
    }
}

