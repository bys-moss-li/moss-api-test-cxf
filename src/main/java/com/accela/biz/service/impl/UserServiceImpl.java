package com.accela.biz.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.accela.biz.model.User;
import com.accela.biz.service.UserService;

public class UserServiceImpl implements UserService
{

	public int create(User user)
	{
		return 0;
	}

	public List<User> listUser()
	{
		List<User> users = new ArrayList<User>();
		
		User u1 = new User("1", "moss", "moss@beyondsoft.com");
		users.add(u1);
		User u2 = new User("2", "moss", "moss@beyondsoft.com");
		users.add(u2);
		User u3 = new User("3", "moss", "moss@beyondsoft.com");
		users.add(u3);
		
		return users;
	}

	public int delete(int id)
	{
		return 0;
	}

}
