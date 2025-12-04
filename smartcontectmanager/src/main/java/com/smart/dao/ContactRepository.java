package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contect;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contect, Integer> {

	//current page
	//contact per page 5
	@Query("from Contect as c where c.user.id=:userId")
	public Page<Contect> findContactsByUser(@Param("userId") int userId,Pageable pageable);
	
	
	public List<Contect> findByNameContainingAndUser(String name,User user);

}
