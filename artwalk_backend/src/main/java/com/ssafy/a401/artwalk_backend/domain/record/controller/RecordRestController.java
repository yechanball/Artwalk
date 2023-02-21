package com.ssafy.a401.artwalk_backend.domain.record.controller;

import java.util.List;

import com.ssafy.a401.artwalk_backend.domain.record.model.*;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.a401.artwalk_backend.domain.admin.model.AdminDTO;
import com.ssafy.a401.artwalk_backend.domain.admin.model.PasswordDTO;
import com.ssafy.a401.artwalk_backend.domain.admin.service.AdminService;
import com.ssafy.a401.artwalk_backend.domain.common.model.CountResponseDTO;
import com.ssafy.a401.artwalk_backend.domain.common.model.ResponseDTO;
import com.ssafy.a401.artwalk_backend.domain.record.model.Record;
import com.ssafy.a401.artwalk_backend.domain.record.service.RecordService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = {"기록 API"}, description = "기록 정보 API 입니다.")
@RestController
@RequestMapping("record")
@RequiredArgsConstructor
public class RecordRestController {
	private static ModelMapper modelMapper = new ModelMapper();
	private static final String OK = "Ok";
	private static final String FAIL = "Fail";
	
	private final RecordService recordService;
	private final AdminService adminService;

	@Operation(summary = "기록 저장", description = "기록 저장 메서드입니다. Request Body 내에 Json 형식으로 duration(double, 시간), distance(double, 거리), geometry(String, polyline encoded 기록 문자열), title(String, 제목)를 넣어 요청을 보내면 기록이 저장됩니다.")
	@PostMapping("")
	public ResponseEntity<RecordResponseDTO> recordAdd(@RequestBody RecordRequestDTO recordRequestDTO, @ApiIgnore Authentication authentication) {
		String userId = authentication.getName();
		Record record = modelMapper.map(recordRequestDTO, Record.class);
		Record result = recordService.addRecord(record, userId);

		if(result != null) return ResponseEntity.ok().body(new RecordResponseDTO(OK, result));
		else return ResponseEntity.badRequest().body(new RecordResponseDTO(FAIL, null));
	}

	@Operation(summary = "기록 조회", description = "기록 조회 메서드입니다. Path에 조회할 기록 ID를 포함하여 요청합니다.")
	@ApiImplicitParam(name = "recordId", value = "조회할 기록 ID", dataType = "int")
	@GetMapping("/{recordId}")
	public ResponseEntity<RecordResponseDTO> recordDetails(@PathVariable("recordId") int recordId) {
		Record record = recordService.findByRecordId(recordId);
		if(record != null) {
			record.setThumbnail(recordService.makeThumbnailUrl(record.getRecordId()));
			record.setRecentImage(recordService.makeImageUrl(record.getRecordId()));
			record.setGeometry(recordService.readGeometryFile(record));
			return ResponseEntity.ok().body(new RecordResponseDTO(OK, record));
		}
		else return ResponseEntity.badRequest().body(new RecordResponseDTO(FAIL, null));
	}

	@Operation(summary = "기록 수정", description = "기록 수정 메서드입니다. Path에 수정할 기록 ID를, Request Body에는 수정할 title(제목) 내용을 포함하여 요청합니다.")
	@ApiImplicitParam(name = "recordId", value = "수정할 기록 ID", dataType = "int")
	@PutMapping("/{recordId}")
	public ResponseEntity<RecordResponseDTO> recordModify(@PathVariable("recordId") int recordId, @RequestBody RecordUpdateRequestDTO recordUpdateRequestDTO) {
		Record originRecord  = recordService.findByRecordId(recordId);
		Record result = recordService.modifyRecord(originRecord, recordUpdateRequestDTO.getTitle());

		if(result != null) return ResponseEntity.ok().body(new RecordResponseDTO(OK, result));
		else return ResponseEntity.badRequest().body(new RecordResponseDTO(FAIL, null));
	}

	@Operation(summary = "기록 삭제", description = "기록 삭제 메서드입니다. Path에 삭제할 기록 ID를 포함하여 요청합니다.")
	@ApiImplicitParam(name = "recordId", value = "삭제할 기록 ID", dataType = "int")
	@DeleteMapping("/{recordId}")
	public ResponseEntity<CountResponseDTO> recordRemove(@PathVariable("recordId") int recordId) {
		Record record = recordService.findByRecordId(recordId);
		long result = recordService.removeRecord(record);

		if(result == 0) return ResponseEntity.ok().body(new CountResponseDTO(OK, result));
		else return ResponseEntity.badRequest().body(new CountResponseDTO(FAIL, result));
	}

	@Operation(summary = "관리자용 사용자 기록 삭제", description = "사용자 기록 삭제 메서드입니다. Path에 삭제할 기록 ID와 request body에 password(관리자 비밀번호)를 담아 요청합니다.")
	@DeleteMapping("/admin/{recordId}")
	public ResponseEntity<CountResponseDTO> recordRemoveAdmin(@RequestBody PasswordDTO passwordDTO , @PathVariable("recordId") int recordId, @ApiIgnore Authentication authentication) {
		Record record = recordService.findByRecordId(recordId);
		AdminDTO adminDTO = modelMapper.map(passwordDTO, AdminDTO.class);
		adminDTO.setUserId(authentication.getName());
		int result = adminService.checkPassword(adminDTO);

		if (result == 0) {
			long res = recordService.removeRecord(record);
			if (res == 0) return ResponseEntity.ok().body(new CountResponseDTO(OK, result));
			else return ResponseEntity.badRequest().body(new CountResponseDTO(FAIL, result));
		}
		else return ResponseEntity.badRequest().body(new CountResponseDTO(FAIL, result));
	}

	@Operation(summary = "기록 목록 조회", description = "기록 목록 조회 메서드입니다. Query String의 user(boolean) 값을 통해 전체 기록 목록 또는 사용자 기록 목록을 반환합니다.")
	@ApiImplicitParam(name = "user", value = "true: accessToken과 일치하는 사용자의 기록 목록을 반환합니다. ||  false: 모든 사용자의 기록 목록을 반환합니다.", dataType = "boolean")
	@GetMapping("/list")
	public ResponseEntity<RecordListResponseDTO> recordList(@RequestParam(name="user") boolean searchForUser, @ApiIgnore Authentication authentication) {
		List<Record> records = null;

		if(searchForUser) {
			String userId = authentication.getName();
			records = recordService.findByUserId(userId);
		} else {
			records = recordService.findAllRecord();
		}

		if(records != null) return ResponseEntity.ok().body(new RecordListResponseDTO(OK, records));
		else return ResponseEntity.badRequest().body(new RecordListResponseDTO(FAIL, null));
	}

	@Operation(summary = "기록 검색", description = "기록 검색 메서드입니다. query string에 type과 keyword를 포함하여 검색 옵션과 키워드에 해당하는 기록 목록을 반환합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "type", value = "기록 검색 옵션 - userId(사용자 아이디), title(기록 제목)", dataType = "String"),
		@ApiImplicitParam(name = "keyword", value = "기록 검색 키워드", dataType = "String")
	})
	@GetMapping("/search")
	public ResponseEntity<RecordListResponseDTO> recordListSearch(@RequestParam(name = "type") String type, @RequestParam(name = "keyword") String keyword) {
		List<Record> records = null;

		if(type.equals("nickname")) records = recordService.findByNickNameContaining(keyword);
		else if (type.equals("title")) records = recordService.findByTitleContaining(keyword);

		if(records != null) return ResponseEntity.ok().body(new RecordListResponseDTO(OK, records));
		else return ResponseEntity.badRequest().body(new RecordListResponseDTO(FAIL, null));
	}

	@Operation(summary = "관리자용 사용자 기록 목록 조회", description = "특정 사용자 기록 목록 조회 메서드입니다. Path에 사용자 ID를 포함하여 요청합니다.")
	@ApiImplicitParam(name = "userId", value = "기록 목록을 조회할 사용자 ID (예시. ssafy@ssafy.com)", dataType = "String")
	@GetMapping("/list/{userId}")
	public ResponseEntity<RecordListResponseDTO> recordListByUserId(@PathVariable("userId") String userId){
		List<Record> records = null;

		records = recordService.findByUserId(userId);

		if(records != null) return ResponseEntity.ok().body(new RecordListResponseDTO(OK, records));
		else return ResponseEntity.badRequest().body(new RecordListResponseDTO(FAIL, null));
	}

	@Operation(summary = "기록 개수 조회", description = "AccessToken과 일치하는 사용자의 기록 개수를 반환합니다.")
	@GetMapping("/count")
	public ResponseEntity<CountResponseDTO> recordCount(@ApiIgnore Authentication authentication) {
		String userId = authentication.getName();
		long count = recordService.getRecordCount(userId);
		return ResponseEntity.ok().body(new CountResponseDTO(OK, count));
	}

	@Operation(summary = "기록 썸네일 조회", description = "기록 썸네일 조회 메서드입니다. Path에 조회하려는 기록 ID를 포함하여 요청합니다.")
	@ApiImplicitParam(name = "recordId", value = "조회할 기록 ID", dataType = "int")
	@GetMapping("/thumb/{recordId}")
	public ResponseEntity<Resource> recordThumbnailDisplay(@PathVariable("recordId") int recordId) {
		Record record = recordService.findByRecordId(recordId);
		ResponseEntity<Resource> response = recordService.getThumbnailImage(record);
		return response;
	}

	@Operation(summary = "공유페이지 생성", description = "공유페이지 생성 메서드입니다. 편집을 위한 주소를 반환합니다. Path에 편집하려는 기록 주소를 포함해 요청합니다.")
	@ApiImplicitParam(name = "recordId", value = "조회할 기록 recordId", dataType = "int")
	@GetMapping("/share/{recordId}")
	public ResponseEntity<ResponseDTO> recordShareImagePageMake(@PathVariable("recordId") int recordId) {
		// recordId로 기록을 찾아온다.
		Record record = recordService.findByRecordId(recordId);
		if (record != null) {
			// 임시 주소 생성한다.
			String randomLink = recordService.saveRandomEditLink(record);
			return ResponseEntity.ok().body(new ResponseDTO(OK, randomLink));
		}
		else return ResponseEntity.badRequest().body(new ResponseDTO(FAIL, null));
	}

	@Operation(summary = "공유이미지 조회", description = "공유이미지 조회 메서드입니다. Path에 조회하려는 기록 ID를 포함하여 요청합니다.")
	@ApiImplicitParam(name = "link", value = "조회할 기록 link", dataType = "String")
	@GetMapping("/image/{link}")
	public ResponseEntity<Resource> recordShareImageDisplay(@PathVariable("link") String link) {
		Record record = recordService.findByLink(link);
		ResponseEntity<Resource> response = recordService.getShareImage(record);
		return response;
	}

	@Operation(summary = "공유이미지 저장/갱신", description = "공유이미지 저장/갱신 메서드입니다. Request Header에 token과 editLink를 포함하고, Request Body에 지도 중심 좌표와 확대, 회전 정도, polyline 굵기와 색상을 포함하여 보냅니다.")
	@PostMapping("/image")
	public ResponseEntity<ResponseDTO> recordShareImageSave(@RequestHeader(value="editLink") String editLink, @RequestBody RecordImageRequestDTO recordImageRequestDTO, @ApiIgnore Authentication authentication) {
		Record record = recordService.findByEditLink(editLink);

		String userId = authentication.getName();

		if(record != null && record.getUserId().equals(userId)){
			Record result = recordService.saveRecordImage(record, recordImageRequestDTO);
			if(result != null) {
				String link = recordService.saveRandomLink(result);
				return ResponseEntity.ok().body(new ResponseDTO(OK, link));
			}
		}
		return ResponseEntity.badRequest().body(new ResponseDTO(FAIL, "공유 이미지 생성에 실패하였습니다.\n다시 시도해주세요."));
	}
}
