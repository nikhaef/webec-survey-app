package com.example.surveyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.surveyapp.model.Response;
import com.example.surveyapp.model.Survey;
import com.example.surveyapp.model.User;
import java.util.Optional;
import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    Optional<Response> findBySurveyAndRespondent(Survey survey, User respondent);
    List<Response> findBySurvey(Survey survey);
}

