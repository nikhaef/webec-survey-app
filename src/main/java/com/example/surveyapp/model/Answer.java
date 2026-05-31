package com.example.surveyapp.model;

import jakarta.persistence.*;

@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Response response;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Option selected;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public Option getSelected() { return selected; }
    public void setSelected(Option selected) { this.selected = selected; }
}

