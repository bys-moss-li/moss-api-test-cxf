package com.carnation.biz.service;

import java.util.List;

import com.carnation.biz.model.User;


public interface UserService
{
	int create(User user);
	
	List<User> listUser();
	
	int delete(int id);
	
	

}
