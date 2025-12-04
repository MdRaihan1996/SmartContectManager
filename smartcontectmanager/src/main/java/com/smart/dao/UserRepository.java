package com.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{
 
	//database query
	//send data by @param annotation
	@Query("select u from User u where u.name= :name")
	public User getUserByUserName(@Param("name") String name);
	
}
