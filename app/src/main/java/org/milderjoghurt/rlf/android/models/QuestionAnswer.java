package org.milderjoghurt.rlf.android.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionAnswer {

    public Long id;

    public Session session;

    public String owner;

    public Date date = new Date();

    public Answer answer;

    public enum Answer {
        A,
        B,
        C,
        D,
    }

    public QuestionAnswer() {
    }

    public QuestionAnswer(Answer answer) {
        this.answer = answer;
    }

}
