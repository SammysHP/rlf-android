package org.milderjoghurt.rlf.android.models;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vote {

	public Long id;

	public Session session;

	public String owner;

	public Date date = new Date();

	public Integer vote;

	public Type type;

	public enum Type {
		@EnumValue("S")
		SPEED,

		@EnumValue("U")
		UNDERSTANDABILITY,

		@EnumValue("R")
		REQUEST,

		@EnumValue("N")
		NOREQUEST,
	}

	public Vote(Session session, String owner, Type type, Integer vote) {
		this.session = session;
		this.owner = owner;
		this.type = type;
		this.vote = vote;
	}

}
