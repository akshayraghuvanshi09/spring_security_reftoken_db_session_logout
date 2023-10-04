package com.springsecuritywithdb.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springsecuritywithdb.entity.RefreshToken;
import com.springsecuritywithdb.entity.UserInfo;
import com.springsecuritywithdb.repository.RefreshTokenRepository;
import com.springsecuritywithdb.repository.UserInfoRepository;

@Service
public class RefreshTokenService {

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserInfoRepository userInfoRepository;

	public RefreshToken createRefreshToken(String username) {
		UserInfo uesr = userInfoRepository.findByName(username).get();
		
		RefreshToken refreshToken = RefreshToken.builder().userInfo(userInfoRepository.findByName(username).get())
				.token(UUID.randomUUID().toString()).expiryDate(Instant.now().plusMillis(600000)).build();

		return refreshTokenRepository.save(refreshToken);
	}
	
	public Optional<RefreshToken> findByToken(String token){
		return refreshTokenRepository.findByToken(token);
	}
	
	public RefreshToken verifyExpiration(RefreshToken token) {
		if (token.getExpiryDate().compareTo(Instant.now())<0) {
			refreshTokenRepository.delete(token);
			throw new RuntimeException(token.getToken()+ " Refresh token was expired. Please make a new signin request");
		}
		return token;
	}
}
