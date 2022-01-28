package com.janek.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.janek.security1.model.User;

// CRUD 함수를 JpaRepository가 들고 있다.
// @Repository라는 어노테이션이 없어도 IoC가 된다.(JpaRepository를 상속했기 때문에)
public interface UserRepository extends JpaRepository<User, Integer> {
	
	public User findByUsername(String username);
	
}
