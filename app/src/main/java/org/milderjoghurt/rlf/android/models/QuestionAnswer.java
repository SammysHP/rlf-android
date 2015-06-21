package org.milderjoghurt.rlf.android.models;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionAnswer {

	public Long id;

	public Session session;

	public String owner;

	public Date date = new Date();

	@Constraints.Required
	public Answer answer;

	public enum Answer {
		@EnumValue("A")
		A,

		@EnumValue("B")
		B,

		@EnumValue("C")
		C,

		@EnumValue("D")
		D,
	}

	public QuestionAnswer(Session session, String owner, Answer answer) {
		this.session = session;
		this.owner = owner;
		this.answer = answer;
	}

}
