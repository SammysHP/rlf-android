package org.milderjoghurt.rlf.android.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vote {

	public Long id;

	public Session session;

	public String owner;

	public Date date = new Date();

	public Integer value;

	public Type type;

	public enum Type {
		SPEED,
		UNDERSTANDABILITY,
		REQUEST,
	}

	public Vote() {
	}

	public Vote(Session session, String owner, Type type, Integer value) {
		this.session = session;
		this.owner = owner;
		this.type = type;
		this.value = value;
	}

}
