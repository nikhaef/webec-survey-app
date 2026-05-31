package surveyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import surveyapp.model.Option;

public interface OptionRepository extends JpaRepository<Option, Long> {
}

