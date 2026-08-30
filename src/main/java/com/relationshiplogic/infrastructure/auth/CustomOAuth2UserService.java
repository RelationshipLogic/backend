package com.relationshiplogic.infrastructure.auth;

import com.relationshiplogic.domain.user.SocialProvider;
import com.relationshiplogic.domain.user.User;
import com.relationshiplogic.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        ProviderUserInfo userInfo = extract(registrationId, oAuth2User.getAttributes());
        SocialProvider provider = SocialProvider.valueOf(registrationId.toUpperCase());

        User user = userRepository.findByProviderAndProviderId(provider, userInfo.providerId())
                .orElseGet(() -> userRepository.save(
                        User.create(provider, userInfo.providerId(), userInfo.email(), userInfo.nickname())));

        return new AppOAuth2User(user.getId(), oAuth2User);
    }

    // provider마다 사용자 정보 응답 구조가 달라서(카카오/네이버는 중첩, 구글은 평면) provider별로 파싱한다.
    private ProviderUserInfo extract(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "kakao" -> extractKakao(attributes);
            case "google" -> extractGoogle(attributes);
            case "naver" -> extractNaver(attributes);
            default -> throw new IllegalStateException("지원하지 않는 provider입니다: " + registrationId);
        };
    }

    @SuppressWarnings("unchecked")
    private ProviderUserInfo extractKakao(Map<String, Object> attributes) {
        String providerId = String.valueOf(attributes.get("id"));
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
        String nickname = profile != null ? (String) profile.get("nickname") : null;

        return new ProviderUserInfo(providerId, email, nickname);
    }

    private ProviderUserInfo extractGoogle(Map<String, Object> attributes) {
        String providerId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("name");

        return new ProviderUserInfo(providerId, email, nickname);
    }

    // 네이버는 실제 사용자 정보가 최상위가 아니라 "response" 키 아래에 중첩되어 내려온다.
    @SuppressWarnings("unchecked")
    private ProviderUserInfo extractNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        String providerId = (String) response.get("id");
        String email = (String) response.get("email");
        String nickname = (String) response.get("name");

        return new ProviderUserInfo(providerId, email, nickname);
    }

    private record ProviderUserInfo(String providerId, String email, String nickname) {
    }
}
