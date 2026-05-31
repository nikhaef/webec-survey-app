package surveyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import surveyapp.model.Response;
import surveyapp.model.Survey;
import surveyapp.model.User;
import java.util.Optional;
import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    Optional<Response> findBySurveyAndRespondent(Survey survey, User respondent);
    List<Response> findBySurvey(Survey survey);
}

