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

    public QuestionAnswer(Session session, String owner, Answer answer) {
        this.session = session;
        this.owner = owner;
        this.answer = answer;
    }

}
