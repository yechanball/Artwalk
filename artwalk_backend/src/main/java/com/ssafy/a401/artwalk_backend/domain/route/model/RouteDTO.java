package com.ssafy.a401.artwalk_backend.domain.route.model;

import java.util.Date;

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
@Schema(description = "경로 DTO")
public class RouteDTO {
	@Schema(description = "경로 ID", nullable = false, example = "6")
	private int routeId;

	@Schema(description = "사용자 ID", nullable = false, example = "ssafy@ssafy.com")
	private String userId;

	@Schema(description = "사용자 닉네임", nullable = false, example = "닉네임")
	private String nickname;

	@Schema(description = "작성자 ID", nullable = false, example = "ssafy@ssafy.com")
	private String maker;

	@Schema(description = "소요 시간", nullable = false, example = "231.12")
	private double duration;

	@Schema(description = "거리", nullable = false, example = "423.23")
	private double distance;

	@Schema(description = "polyline 인코딩된 경로", example = "}e{cFemjfWaA}EdEgBh@yAw@yDlPeG??SF{@_EuAf@w@cEoC~@y@cEkAb@??oJnDIIe@oBcA^m@uC`@Q??`@MHQg@}BaA^e@kC]N@H_@NUKw@^Cn@P|@d@QnChM}FbCrAhGn@xByEfB??o@TvCnNdDmATdAjA[AW`AI??z@IZOFZvB{@dBhIh@Ow@mDd@O")
	private String geometry;

	private Date creation;

	@Schema(description = "경로 썸네일 요청 경로", example = "/route/thumb/6")
	private String thumbnail;

	@Schema(description = "경로 제목", example = "나의 멋진 경로")
	private String title;
}
