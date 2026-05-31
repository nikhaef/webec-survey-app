package surveyapp.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import surveyapp.model.Survey;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SurveyRepositoryTest {

    @Autowired
    private SurveyRepository surveyRepository;

    @BeforeEach
    void setUp() {
        surveyRepository.deleteAll();
    }

    @Test
    void testFindByOpenTrue() {
        Survey open1 = new Survey();
        open1.setTitle("Open Survey 1");
        open1.setOpen(true);
        surveyRepository.save(open1);

        Survey closed = new Survey();
        closed.setTitle("Closed Survey");
        closed.setOpen(false);
        surveyRepository.save(closed);

        List<Survey> openSurveys = surveyRepository.findByOpenTrue();
        assertEquals(1, openSurveys.size());
        assertEquals("Open Survey 1", openSurveys.get(0).getTitle());
    }

    @Test
    void testFindByOpenFalse() {
        Survey open = new Survey();
        open.setTitle("Open Survey");
        open.setOpen(true);
        surveyRepository.save(open);

        Survey closed1 = new Survey();
        closed1.setTitle("Closed Survey 1");
        closed1.setOpen(false);
        surveyRepository.save(closed1);

        List<Survey> closedSurveys = surveyRepository.findByOpenFalse();
        assertEquals(1, closedSurveys.size());
        assertEquals("Closed Survey 1", closedSurveys.get(0).getTitle());
    }

    @Test
    void testSurveyTitlePersistence() {
        Survey survey = new Survey();
        survey.setTitle("Test Title");
        survey.setOpen(true);
        surveyRepository.save(survey);

        Survey fetched = surveyRepository.findById(survey.getId()).orElse(null);
        assertNotNull(fetched);
        assertEquals("Test Title", fetched.getTitle());
        assertTrue(fetched.isOpen());
    }
}

