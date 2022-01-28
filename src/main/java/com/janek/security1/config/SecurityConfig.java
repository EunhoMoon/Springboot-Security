package com.janek.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.janek.security1.config.oauth.PrincipalOauth2UserService;


/*
 * 1. 코드받기(인증)
 * 2. 엑세스 토큰(권한)
 * 3. 사용자 프로필 정보
 * 4-1. 회원가입을 자동으로 진행시키기도 함 / 4-2. 추가 구성 정보를 받는 작업(주소, 등급 등) 
*/

@Configuration
@EnableWebSecurity	// 활성화, 스프링 시큐리티 필터가 스프링 필터체인에 등록됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)	// securedEnabled : secured 어노테이션 활성화 / prePostEnabled = preAuthorize 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	@Bean // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {	// 권한 설정
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated()
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll()	// 이 외의 주소는 허용
			.and()
			.formLogin()
			.loginPage("/loginForm")
			.loginProcessingUrl("/login")	// '/login'이라는 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
			.defaultSuccessUrl("/")			// 성공하면 메인 페이지로 이동
			.and()
			.oauth2Login()
			.loginPage("/loginForm")	// 구글 로그인 완료된 후 후처리 (tip. 코드X(엑세스 토큰 + 사용자 프로필 정보 O)
			.userInfoEndpoint()
			.userService(principalOauth2UserService);
	}
	
}
