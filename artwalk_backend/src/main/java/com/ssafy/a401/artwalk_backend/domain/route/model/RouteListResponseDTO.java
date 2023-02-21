package com.ssafy.a401.artwalk_backend.domain.route.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "경로 목록 응답 DTO")
public class RouteListResponseDTO {
	@Schema(description = "응답 코드", nullable = false, example = "Ok")
	private String code;

	@Schema(description = "경로 배열", example = "요청에 따른 경로 목록 데이터 배열")
	private List<Route> routes;
}
