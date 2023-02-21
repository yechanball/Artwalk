package com.ssafy.a401.artwalk_backend.domain.route.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ssafy.a401.artwalk_backend.domain.common.service.FileService;
import com.ssafy.a401.artwalk_backend.domain.route.model.Route;
import com.ssafy.a401.artwalk_backend.domain.route.repository.RouteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteService {

	private final RouteRepository routeRepository;
	private final FileService fileService;

	private static String fileOption = "route";

	public Route addRoute(Route route, String userId) {
		Route result = null;

		route.setUserId(userId);
		route.setMaker(userId); // TODO: 경로 사용자/최초생성자 관련 기능 추후 추가 예정

		String geometry = route.getGeometry();
		String geometryPath = fileService.saveFile(fileOption, geometry, userId);
		route.setGeometry(geometryPath);

		String thumbPath = fileService.saveThumbnail(fileOption, geometryPath, geometry, userId);
		route.setThumbnail(thumbPath);

		result = routeRepository.save(route);

		return result;
	}

	public Route modifyRoute(Route originRoute, Route newRoute, String userId) {
		Route result = null;

		originRoute.setUserId(userId);

		String updateGeometry = newRoute.getGeometry();
		String geometryPath = fileService.saveFile(fileOption, updateGeometry, userId);
		fileService.removeFile(fileOption, originRoute.getGeometry(), userId);
		originRoute.setGeometry(geometryPath);

		String thumbPath = fileService.saveThumbnail(fileOption, geometryPath, updateGeometry, userId);
		fileService.removeFile(fileOption, originRoute.getThumbnail(), userId);
		originRoute.setThumbnail(thumbPath);

		result = routeRepository.save(originRoute);

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
	public List<Route> findAllRoute() {
		List<Route> routeList = new ArrayList<>();
		List<Route> routes = routeRepository.findAll(Sort.by(Sort.Direction.DESC, "routeId"));
		for (Route route : routes) {
			route.setThumbnail(makeThumbnailUrl(route.getRouteId()));
			route.setGeometry(fileService.readFile(fileOption, route.getGeometry(), route.getUserId()));
			routeList.add(route);
		}
		return routeList;
	}

	/** 저장된 경로 중 route_id가 일치하는 경로를 반환합니다. */
	public Route findByRouteId(int routeId) {
		Route route = routeRepository.findById(routeId).get();
		return route;
	}

	/** 저장된 경로 중 user_id가 일치하는 경로를 반환합니다. */
	public List<Route> findByUserId(String userId) {
		List<Route> routeList = new ArrayList<>();
		List<Route> routes = routeRepository.findByUserIdOrderByRouteIdDesc(userId);
		for (Route route : routes) {
			route.setThumbnail(makeThumbnailUrl(route.getRouteId()));
			route.setGeometry(fileService.readFile(fileOption, route.getGeometry(), userId));
			routeList.add(route);
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

	public List<Route> findByUserIdContaining(String userId) {
		List<Route> routeList = new ArrayList<>();
		List<Route> routes = routeRepository.findByUserIdContainingOrderByRouteIdDesc(userId);
		for (Route route : routes) {
			route.setThumbnail(makeThumbnailUrl(route.getRouteId()));
			route.setGeometry(fileService.readFile(fileOption, route.getGeometry(), userId));
			routeList.add(route);
		}
		return routeList;
	}

	public List<Route> findByMakerContaining(String maker) {
		List<Route> routeList = new ArrayList<>();
		List<Route> routes = routeRepository.findByMakerContainingOrderByRouteIdDesc(maker);
		for (Route route : routes) {
			route.setThumbnail(makeThumbnailUrl(route.getRouteId()));
			route.setGeometry(fileService.readFile(fileOption, route.getGeometry(), route.getUserId()));
			routeList.add(route);
		}
		return routeList;
	}

	public List<Route> findByTitleContaining(String title) {
		List<Route> routeList = new ArrayList<>();
		List<Route> routes = routeRepository.findByTitleContainingOrderByRouteIdDesc(title);
		for (Route route : routes) {
			route.setThumbnail(makeThumbnailUrl(route.getRouteId()));
			route.setGeometry(fileService.readFile(fileOption, route.getGeometry(), route.getUserId()));
			routeList.add(route);
		}
		return routeList;
	}
}
