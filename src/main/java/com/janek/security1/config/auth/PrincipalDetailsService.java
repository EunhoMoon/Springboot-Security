package com.janek.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.janek.security1.model.User;
import com.janek.security1.repository.UserRepository;

@Service
public class PrincipalDetailsService implements UserDetailsService {
// 시큐리티 설정에서 loginProcessingUrl("/login") 
// login요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername 함수가 실행

	@Autowired
	private UserRepository userRepository;
	
	// Security session(Authentication(UserDetails))
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userEntity = userRepository.findByUsername(username);
		
		if (userEntity != null) {
			return new PrincipalDetails(userEntity);
		}
		
		return null;
	}
	
}