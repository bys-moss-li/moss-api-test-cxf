package com.accela.biz.service;

import java.util.List;

import com.accela.biz.model.User;

public interface UserService
{
	int create(User user);
	
	List<User> listUser();
	
	int delete(int id);
	
	

}
