package com.janek.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.janek.security1.model.User;

import lombok.Data;

/* 
 * 1. 시큐리티가 '/login'을 가로채서 로그인을 진행시킨다.
 * 2. 로그인이 진행이 완료되면 시큐리티 session을 만들어준다.(Security ContextHolder)
 * 3. 이 session에 들어갈 수 있는 Object는 Authentication 객체
 * 4. Authentication 객체 안에 User 정보가 있어야 한다.
 * 5. User Object의 타입은  UserDetails 타입의 객체여야 한다.
 * 
 * - Security Session => Authentication => (UserDetails)User
 * - PrincipalDetails가 UserDetails를 implements하면 PrincipalDetails 타입의 User를 사용할 수 있다.
 * - Authentication 객체가 들고 있는 UserDetails과 OAuth2User를 모두 하나의 타입으로 사용하기 위해 두 인터페이스 모두 구현한다.
*/

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

	private User user;	// 콤포지션
	private Map<String, Object> attributes;
	
	// 일반 로그인을 위한 생성자
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	// OAuth 로그인을 위한 생성자
	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	// 해당 User의 권한을 리턴하는 곳
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		
		collect.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
	// 1년이 지나면 휴면 계정으로 전환하는 시스템이라면 false가 될 수 있도록
	
		// 현재시간 - 최종 로그인 => 1년이 지났으면 false가 되도록 세팅
		
		return true;
	}

	// OAuth2User 인터페이스를 구현하기 위한 Override 메소드들
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return attributes.get("sub").toString();
	}
	
}
