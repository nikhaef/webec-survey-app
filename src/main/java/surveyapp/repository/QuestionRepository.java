package surveyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import surveyapp.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}

