package com.janek.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.janek.security1.config.auth.PrincipalDetails;
import com.janek.security1.config.oauth.provider.FacebookUserInfo;
import com.janek.security1.config.oauth.provider.GoogleUserInfo;
import com.janek.security1.config.oauth.provider.NaverUserInfo;
import com.janek.security1.config.oauth.provider.OAuth2UserInfo;
import com.janek.security1.model.User;
import com.janek.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리 함수
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("registration 정보, 어떤 OAuth로 로그인 했는지 : " + userRequest.getClientRegistration());
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		// 구글 로그인 (요청)-> code리턴(OAuth-Client라이브러리) (요청)-> AcessToken 요청 => 여기 까지가 userRequest 정보
		// userRequest 정보 (요청)-> loadUser함수 (요청)-> 구글 회원 프로필 
		System.out.println("getAttribute : " + oAuth2User.getAttributes());
		
		OAuth2UserInfo oAuth2UserInfo = null;
		if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		} else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("페이스북 로그인 요청");
			oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
		} else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
		} else {
			System.out.println("구글이나 페이스북, 네이버만 지원합니다.");
		}
		
		String provider = oAuth2UserInfo.getProvider();	// google, facebook
		String providerId = oAuth2UserInfo.getProviderId();
		String username = provider + "_" + providerId;
		String password = "janek";
		String email = oAuth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if (userEntity == null) {
			System.out.println("OAuth 로그인이 최초입니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.prociderId(providerId)
					.build();
			
			userRepository.save(userEntity);
		} else {
			System.out.println("로그인을 이미 한적이 있습니다. 자동 회원가입이 되어있습니다.");
		}
		
		return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
	}
	
}
