package com.ssafy.a401.artwalk_backend.domain.record.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ssafy.a401.artwalk_backend.domain.record.model.Record;
import com.ssafy.a401.artwalk_backend.domain.record.service.RecordService;
import com.ssafy.a401.artwalk_backend.domain.user.model.UserDTO;
import com.ssafy.a401.artwalk_backend.domain.user.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Api(tags = {"기록 공유 API"}, description = "기록 공유 페이지 이동 API 입니다.")
@Controller
@RequestMapping("sharing")
@RequiredArgsConstructor
public class RecordController {
	private final RecordService recordService;
	private final UserService userService;

	@Operation(summary = "편집 페이지 이동", description = "공유 이미지 편집 페이지 이동 메서드입니다.")
	@GetMapping("/edit/{editLink}")
	public String editShareImage(Model model, @PathVariable("editLink") String editLink) {
		Record record = recordService.findByEditLink(editLink);
		if (record != null) {
			String geometry = recordService.readGeometryFile(record);
			UserDTO userDTO = userService.findUserDetail(record.getUserId());

			model.addAttribute("title", record.getTitle());
			model.addAttribute("geometry", geometry);
			model.addAttribute("editLink", editLink);
			model.addAttribute("token", userDTO.getRefreshToken());
			return "share/makeShare";
		} else {
			return "error/4xx";
		}
	}

	@Operation(summary = "공유페이지 이동", description = "공유페이지 이동 메서드입니다.")
	@GetMapping("/{link}")
	public String sharingRecordPage(Model model, @PathVariable("link") String link) {
		Record record = recordService.findByLink(link);
		if (record != null) {
			model.addAttribute("result", link);

			UserDTO userDTO = userService.findUserDetail(record.getUserId());
			String title = record.getTitle();
			String nickname = userDTO.getNickname();

			if(title.equals("")) title = "나의 기록";
			if(nickname.equals("")) nickname = "익명의 사용자";

			model.addAttribute("title", record.getTitle());
			model.addAttribute("nickname", userDTO.getNickname());
			return "share/sharing";
		} else {
			return "error/4xx";
		}
	}
}
