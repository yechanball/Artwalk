package com.ssafy.a401.artwalk_backend.domain.route.controller;

import java.util.List;

import io.swagger.annotations.ApiImplicitParams;

import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.a401.artwalk_backend.domain.admin.model.AdminDTO;
import com.ssafy.a401.artwalk_backend.domain.admin.model.PasswordDTO;
import com.ssafy.a401.artwalk_backend.domain.admin.service.AdminService;
import com.ssafy.a401.artwalk_backend.domain.common.model.CountResponseDTO;
import com.ssafy.a401.artwalk_backend.domain.route.model.RouteListResponseDTO;
import com.ssafy.a401.artwalk_backend.domain.route.model.RouteRequestDTO;
import com.ssafy.a401.artwalk_backend.domain.route.model.RouteResponseDTO;
import com.ssafy.a401.artwalk_backend.domain.route.service.RouteService;
import com.ssafy.a401.artwalk_backend.domain.route.model.Route;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = {"경로 API"}, description = "경로 정보 API 입니다.")
@RestController
@RequestMapping("route")
@RequiredArgsConstructor
public class RouteRestController {
	private static ModelMapper modelMapper = new ModelMapper();
	private static final String OK = "Ok";
	private static final String FAIL = "Fail";

	private final RouteService routeService;
	private final AdminService adminService;

	@Operation(summary = "경로 저장", description = "경로 저장 메서드입니다. Request Body 내에 Json 형식으로 duration(double, 시간), distance(double, 거리), geometry(String, polyline encoded 경로 문자열), title(String, 제목)를 넣어 요청을 보내면 경로가 저장됩니다.")
	@PostMapping("")
	public ResponseEntity<RouteResponseDTO> routeAdd(@RequestBody RouteRequestDTO routeRequestDTO, @ApiIgnore Authentication authentication){
		String userId = authentication.getName();
		Route route = modelMapper.map(routeRequestDTO, Route.class);
		Route result = routeService.addRoute(route, userId);

		if(result != null) return ResponseEntity.ok().body(new RouteResponseDTO(OK, result));
		else return ResponseEntity.badRequest().body(new RouteResponseDTO(FAIL, null));
	}

	@Operation(summary = "경로 조회", description = "경로 조회 메서드입니다. Path에 조회할 경로 ID를 포함하여 요청합니다.")
	@ApiImplicitParam(name = "routeId", value = "조회할 경로 ID", dataType = "int")
	@GetMapping("/{routeId}")
	public ResponseEntity<RouteResponseDTO> routeDetails(@PathVariable("routeId") int routeId) {
		Route route = routeService.findByRouteId(routeId);
		if(route != null) {
			route.setThumbnail(routeService.makeThumbnailUrl(route.getRouteId()));
			route.setGeometry(routeService.readGeometryFile(route));
			return ResponseEntity.ok().body(new RouteResponseDTO(OK, route));
		}
		else return ResponseEntity.badRequest().body(new RouteResponseDTO(FAIL, null));
	}

	@Operation(summary = "경로 수정", description = "경로 수정 메서드입니다. Path에 수정할 경로 ID를, Request Body에는 수정할 duration(double, 시간), distance(double, 거리), geometry(String, URL 인코딩된 경로 문자열), title(String, 제목)을 포함하여 요청합니다.")
	@ApiImplicitParam(name = "routeId", value = "수정할 경로 ID", dataType = "int")
	@PutMapping("/{routeId}")
	public ResponseEntity<RouteResponseDTO> routeModify(@PathVariable("routeId") int routeId, @RequestBody RouteRequestDTO routeRequestDTO, @ApiIgnore Authentication authentication) {
		String userId = authentication.getName();
		Route originRoute  = routeService.findByRouteId(routeId);
		Route route = modelMapper.map(routeRequestDTO, Route.class);
		Route result = routeService.modifyRoute(originRoute, route, userId);

		if(result != null) return ResponseEntity.ok().body(new RouteResponseDTO(OK, result));
		else return ResponseEntity.badRequest().body(new RouteResponseDTO(FAIL, null));
	}

	@Operation(summary = "경로 삭제", description = "경로 삭제 메서드입니다. Path에 삭제할 경로 ID를 포함하여 요청합니다.")
	@ApiImplicitParam(name = "routeId", value = "삭제할 경로 ID", dataType = "int")
	@DeleteMapping("/{routeId}")
	public ResponseEntity<CountResponseDTO> routeRemove(@PathVariable("routeId") int routeId) {
		Route route = routeService.findByRouteId(routeId);
		long result = routeService.removeRoute(route);

		if(result == 0) return ResponseEntity.ok().body(new CountResponseDTO(OK, result));
		else return ResponseEntity.badRequest().body(new CountResponseDTO(FAIL, result));
	}

	@Operation(summary = "관리자용 사용자 경로 삭제", description = "사용자 경로 삭제 메서드입니다. Path에 삭제할 기록 ID와 request body에 password(관리자 비밀번호)를 담아 요청합니다.")
	@DeleteMapping("/admin/{routeId}")
	public ResponseEntity<CountResponseDTO> routeRemoveAdmin(@RequestBody PasswordDTO passwordDTO, @PathVariable("routeId") int routeId, @ApiIgnore Authentication authentication) {
		Route route = routeService.findByRouteId(routeId);
		AdminDTO adminDTO = modelMapper.map(passwordDTO, AdminDTO.class);
		adminDTO.setUserId(authentication.getName());
		int result = adminService.checkPassword(adminDTO);

		if (result == 0) {
			long res = routeService.removeRoute(route);
			if (res == 0) return ResponseEntity.ok().body(new CountResponseDTO(OK, result));
			else return ResponseEntity.badRequest().body(new CountResponseDTO(FAIL, result));
		}
		else return ResponseEntity.badRequest().body(new CountResponseDTO(FAIL, result));
	}

	@Operation(summary = "경로 목록 조회", description = "경로 목록 조회 메서드입니다. Query String의 user(boolean) 값을 통해 전체 경로 목록 또는 사용자 경로 목록을 반환합니다.")
	@ApiImplicitParam(name = "user", value = "true: accessToken과 일치하는 사용자의 경로 목록을 반환합니다. ||  false: 모든 사용자의 경로 목록을 반환합니다.", dataType = "boolean")
	@GetMapping("/list")
	public ResponseEntity<RouteListResponseDTO> routeList(@RequestParam(name="user") boolean searchForUser, @ApiIgnore Authentication authentication) {
		List<Route> routes = null;

		if(searchForUser) {
			String userId = authentication.getName();
			routes = routeService.findByUserId(userId);
		}else {
			routes = routeService.findAllRoute();
		}

		if(routes != null) return ResponseEntity.ok().body(new RouteListResponseDTO(OK, routes));
		else return ResponseEntity.badRequest().body(new RouteListResponseDTO(FAIL, null));
	}

	@Operation(summary = "경로 검색", description = "경로 검색 메서드입니다. query string에 type과 keyword를 포함하여 검색 옵션과 키워드에 해당하는 경로 목록을 반환합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "type", value = "경로 검색 옵션 - userId(사용자 아이디), maker(경로 제작자), title(경로 제목)", dataType = "String"),
		@ApiImplicitParam(name = "keyword", value = "경로 검색 키워드", dataType = "String")
	})
	@GetMapping("/search")
	public ResponseEntity<RouteListResponseDTO> routeListSearch(@RequestParam(name = "type") String type, @RequestParam(name = "keyword") String keyword) {
		List<Route> routes = null;

		if(type.equals("userId")) routes = routeService.findByUserIdContaining(keyword);
		else if (type.equals("maker")) routes = routeService.findByMakerContaining(keyword);
		else if (type.equals("title")) routes = routeService.findByTitleContaining(keyword);

		if(routes != null) return ResponseEntity.ok().body(new RouteListResponseDTO(OK, routes));
		else return ResponseEntity.badRequest().body(new RouteListResponseDTO(FAIL, null));
	}

	@Operation(summary = "관리자용 사용자 경로 목록 조회", description = "특정 사용자 경로 목록 조회 메서드입니다. Path에 사용자 ID를 포함하여 요청합니다.")
	@ApiImplicitParam(name = "userId", value = "경로 목록을 조회할 사용자 ID (예시. ssafy@ssafy.com)", dataType = "String")
	@GetMapping("/list/{userId}")
	public ResponseEntity<RouteListResponseDTO> routeListByUserId(@PathVariable("userId") String userId){
		List<Route> routes = null;

		routes = routeService.findByUserId(userId);

		if(routes != null) return ResponseEntity.ok().body(new RouteListResponseDTO(OK, routes));
		else return ResponseEntity.badRequest().body(new RouteListResponseDTO(FAIL, null));
	}

	@Operation(summary = "경로 개수 조회", description = "AccessToken과 일치하는 사용자의 경로 개수를 반환합니다.")
	@GetMapping("/count")
	public ResponseEntity<CountResponseDTO> routeCount(@ApiIgnore Authentication authentication) {
		String userId = authentication.getName();
		long count = routeService.getRouteCount(userId);
		return ResponseEntity.ok().body(new CountResponseDTO(OK, count));
	}

	@Operation(summary = "경로 썸네일 조회", description = "경로 썸네일 조회 메서드입니다. Path에 조회하려는 경로 ID를 포함하여 요청합니다.")
	@ApiImplicitParam(name = "routeId", value = "조회할 경로 ID", dataType = "int")
	@GetMapping("/thumb/{routeId}")
	public ResponseEntity<Resource> displayRouteThumbnail(@PathVariable("routeId") int routeId) {
		Route route = routeService.findByRouteId(routeId);
		ResponseEntity<Resource> response = routeService.getThumbnailImage(route);
		return response;
	}
}
