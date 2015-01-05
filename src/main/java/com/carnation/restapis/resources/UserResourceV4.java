package com.carnation.restapis.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.carnation.biz.model.User;
import com.carnation.biz.service.impl.UserServiceImpl;



@Path("/v4/users")
@Produces("application/json")
public class UserResourceV4
{
	@GET
	public String getUserList()
	{
		System.out.println("moss0001");
		return new UserServiceImpl().listUser().toString();
	}
	
	@POST
	public User createUser(User user)
	{
		if (null == user)
		{
			user = new User();
		}
		else
		{
			System.out.println("get user from request.");
			
		}
		
		user.setId("001");
		user.setName("moss001");
		user.setEmail("moss@test.com");
		
		return user;
		
	}

}
