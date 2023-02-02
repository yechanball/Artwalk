package com.ssafy.a401.artwalk_backend.domain.admin.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.ssafy.a401.artwalk_backend.domain.admin.model.Admin;
import com.ssafy.a401.artwalk_backend.domain.admin.repository.AdminRepository;
import com.ssafy.a401.artwalk_backend.domain.token.model.Token;
import com.ssafy.a401.artwalk_backend.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminRepository adminRepository;
	private final UserService userService;

	@Transactional
	public Token login(String userId, String password) {
		Optional<Admin> admins = adminRepository.findById(userId);

		if (admins.isPresent()) {
			Admin admin = admins.get();
			if (password.equals(admin.getPassword())) {
				Authentication authentication = userService.getAuthentication(userId, password, "ROLE_ADMIN");
				Token token = userService.getToken(authentication);
				admin.setRefreshToken(token.getRefreshToken());
				return token;
			}
			else {
				log.info("패스워드 불일치");
			}
		}
		else {
			log.info("관리자 아이디 틀림");
		}

		return null;
	}
}