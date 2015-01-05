package com.carnation.biz.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class User
{
	public User()
	{
		super();
	}

	public User(String id, String name, String email)
	{
		super();
		this.id = id;
		this.name = name;
		this.email = email;
	}

	private String id;
	
	@JsonView
	@JsonProperty(value = "name")
	private String name;
	
	@JsonView
	private String email;

	@JsonView
	public String getId()
	{
		return id;
	}

	@JsonView
	public void setId(String id)
	{
		this.id = id;
	}

	@JsonView
	@JsonProperty(value = "name")
	public String getName()
	{
		return name;
	}

	@JsonView
	public void setName(String name)
	{
		this.name = name;
	}
	
	@JsonView
	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

}
