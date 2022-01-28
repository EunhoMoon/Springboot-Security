package com.janek.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.janek.security1.config.auth.PrincipalDetails;
import com.janek.security1.model.User;
import com.janek.security1.repository.UserRepository;

@Controller	// view를 리턴하겠다.
public class IndexController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin (
			Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) 
	{	// 의존성 주입 / @AuthenticationPrincipal : 세션 정보에 접근할 수 있다.
		System.out.println("========================== /test/login ==========================");
		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();
		System.out.println("authentication : " + principalDetails.getUser());
		
		System.out.println("userDetails : " + userDetails.getUser());
		return "세션 정보 확인";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOauthLogin (
			Authentication authentication, @AuthenticationPrincipal OAuth2User oauth) 
	{	// 의존성 주입 / @AuthenticationPrincipal : 세션 정보에 접근할 수 있다.
		System.out.println("========================== /test/oauth/login ==========================");
		OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();
		System.out.println("authentication : " + oauth2User.getAttributes());
		
		System.out.println("oauth2User : " + oauth.getAttributes());
		return "OAuth2User 정보 확인";
	}
	
	@GetMapping({"", "/"})
	public String index() {
		// mustache : 스프링이 권장하는 템플릿. 기본 폴더 = src/main/resources/
		// view resolver 설정 : templates (prefix), .mustach(suffix)
		return "index";
	}
	
	// 일반 로그인을 해도 PrincipalDetails 로 받을 수 있고,
	// OAuth 로그인을 해도 PrincipalDetails 로 받을 수 있다.
	@GetMapping("/user")
	public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : " + principalDetails.getUser());
		return "user";
	}
	
	@GetMapping("/admin")
	public String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public String manager() {
		return "manager";
	}
	
	@GetMapping("/loginForm")
	public String login() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");
		
		// password 암호화(해시)
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		
		userRepository.save(user);
		
		return "redirect:/loginForm";
		// redirect: = 뒤에 붙어있는 주소를 호출해준다.
	}
	
	@Secured("ROLE_ADMIN")	// 글로벌로 지정된 페이지 외에 권한을 부여하고 싶을 때 사용
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인 정보 페이지";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	// 글로벌로 지정된 페이지 외에 여러 개이의 권한을 부여하고 싶을 때 사용
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터 페이지";
	}
	
}
