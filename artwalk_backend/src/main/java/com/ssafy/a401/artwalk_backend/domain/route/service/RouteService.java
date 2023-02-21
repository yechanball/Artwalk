package com.ssafy.a401.artwalk_backend.domain.route.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ssafy.a401.artwalk_backend.domain.common.service.FileService;
import com.ssafy.a401.artwalk_backend.domain.route.model.Route;
import com.ssafy.a401.artwalk_backend.domain.route.model.RouteDTO;
import com.ssafy.a401.artwalk_backend.domain.route.repository.RouteRepository;
import com.ssafy.a401.artwalk_backend.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteService {
	private static ModelMapper modelMapper = new ModelMapper();
	private final RouteRepository routeRepository;
	private final FileService fileService;
	private final UserService userService;

	private static String fileOption = "route";

	public RouteDTO addRoute(Route route, String userId) {
		RouteDTO result = null;

		route.setUserId(userId);
		route.setMaker(userId); // TODO: 경로 사용자/최초생성자 관련 기능 추후 추가 예정

		String geometry = route.getGeometry();
		String geometryPath = fileService.saveFile(fileOption, geometry, userId);
		route.setGeometry(geometryPath);

		String thumbPath = fileService.saveThumbnail(fileOption, geometryPath, geometry, userId);
		route.setThumbnail(thumbPath);

		Route saveRoute = routeRepository.save(route);

		result = modelMapper.map(saveRoute, RouteDTO.class);
		result.setNickname(userService.findUserDetail(result.getUserId()).getNickname());

		return result;
	}

	public RouteDTO modifyRoute(Route originRoute, Route newRoute, String userId) {
		RouteDTO result = null;

		originRoute.setUserId(userId);

		String updateGeometry = newRoute.getGeometry();
		String geometryPath = fileService.saveFile(fileOption, updateGeometry, userId);
		fileService.removeFile(fileOption, originRoute.getGeometry(), userId);
		originRoute.setGeometry(geometryPath);

		String thumbPath = fileService.saveThumbnail(fileOption, geometryPath, updateGeometry, userId);
		fileService.removeFile(fileOption, originRoute.getThumbnail(), userId);
		originRoute.setThumbnail(thumbPath);

		Route saveRoute = routeRepository.save(originRoute);
		result = modelMapper.map(saveRoute, RouteDTO.class);

		return result;
	}

	public long removeRoute(Route route) {
		routeRepository.delete(route);
		fileService.removeFile(fileOption, route.getGeometry(), route.getUserId());
		fileService.removeFile(fileOption, route.getThumbnail(), route.getUserId());
		long result = routeRepository.countByRouteId(route.getRouteId());
		return result;
	}

	/** 저장된 경로의 개수를 반환합니다. */
	public long getRouteCount(String userId) {
		long count = routeRepository.countByUserId(userId);
		return count;
	}

	/** 저장된 모든 경로를 반환합니다. */
	public List<RouteDTO> findAllRoute() {
		List<RouteDTO> routeList = new ArrayList<>();
		List<Route> routes = routeRepository.findAll(Sort.by(Sort.Direction.DESC, "routeId"));
		for (Route route : routes) {
			RouteDTO routeItem = modelMapper.map(route, RouteDTO.class);
			routeItem.setNickname(userService.findUserDetail(route.getUserId()).getNickname());
			routeItem.setThumbnail(makeThumbnailUrl(route.getRouteId()));
			routeItem.setGeometry(fileService.readFile(fileOption, route.getGeometry(), route.getUserId()));
			routeList.add(routeItem);
		}
		return routeList;
	}

	/** 저장된 경로 중 route_id가 일치하는 경로를 반환합니다. */
	public RouteDTO findByRouteId(int routeId) {
		Route route = routeRepository.findById(routeId).get();

		RouteDTO result = modelMapper.map(route, RouteDTO.class);
		result.setNickname(userService.findUserDetail(result.getUserId()).getNickname());

		return result;
	}

	/** 저장된 경로 중 user_id가 일치하는 경로를 반환합니다. */
	public List<RouteDTO> findByUserId(String userId) {
		List<RouteDTO> routeList = new ArrayList<>();
		List<Route> routes = routeRepository.findByUserIdOrderByRouteIdDesc(userId);
		for (Route route : routes) {
			RouteDTO routeItem = modelMapper.map(route, RouteDTO.class);
			routeItem.setNickname(userService.findUserDetail(route.getUserId()).getNickname());
			routeItem.setThumbnail(makeThumbnailUrl(route.getRouteId()));
			routeItem.setGeometry(fileService.readFile(fileOption, route.getGeometry(), userId));
			routeList.add(routeItem);
		}
		return routeList;
	}

	public String readGeometryFile(Route route) {
		return fileService.readFile(fileOption, route.getGeometry(), route.getUserId());
	}

	public ResponseEntity<Resource> getThumbnailImage(Route route) {
		return fileService.findThumbnail(fileOption, route.getThumbnail(), route.getUserId());
	}

	/** 썸네일 요청 경로를 반환합니다. */
	public static String makeThumbnailUrl(int routeId) {
		String thumbUrl = "/route/thumb/" + routeId;
		return thumbUrl;
	}

	public List<RouteDTO> findByUserIdContaining(String userId) {
		List<RouteDTO> routeList = new ArrayList<>();
		List<Route> routes = routeRepository.findByUserIdContainingOrderByRouteIdDesc(userId);
		for (Route route : routes) {
			RouteDTO routeItem = modelMapper.map(route, RouteDTO.class);
			routeItem.setNickname(userService.findUserDetail(route.getUserId()).getNickname());
			routeItem.setThumbnail(makeThumbnailUrl(route.getRouteId()));
			routeItem.setGeometry(fileService.readFile(fileOption, route.getGeometry(), userId));
			routeList.add(routeItem);
		}
		return routeList;
	}

	public List<RouteDTO> findByMakerContaining(String maker) {
		List<RouteDTO> routeList = new ArrayList<>();
		List<Route> routes = routeRepository.findByMakerContainingOrderByRouteIdDesc(maker);
		for (Route route : routes) {
			RouteDTO routeItem = modelMapper.map(route, RouteDTO.class);
			routeItem.setNickname(userService.findUserDetail(route.getUserId()).getNickname());
			routeItem.setThumbnail(makeThumbnailUrl(route.getRouteId()));
			routeItem.setGeometry(fileService.readFile(fileOption, route.getGeometry(), route.getUserId()));
			routeList.add(routeItem);
		}
		return routeList;
	}

	public List<RouteDTO> findByTitleContaining(String title) {
		List<RouteDTO> routeList = new ArrayList<>();
		List<Route> routes = routeRepository.findByTitleContainingOrderByRouteIdDesc(title);
		for (Route route : routes) {
			RouteDTO routeItem = modelMapper.map(route, RouteDTO.class);
			routeItem.setNickname(userService.findUserDetail(route.getUserId()).getNickname());
			routeItem.setThumbnail(makeThumbnailUrl(route.getRouteId()));
			routeItem.setGeometry(fileService.readFile(fileOption, route.getGeometry(), route.getUserId()));
			routeList.add(routeItem);
		}
		return routeList;
	}
}
