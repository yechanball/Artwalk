package com.ssafy.a401.artwalk_backend.domain.record.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.transaction.Transactional;

import com.ssafy.a401.artwalk_backend.domain.user.model.User;
import com.ssafy.a401.artwalk_backend.domain.user.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ssafy.a401.artwalk_backend.domain.common.service.FileService;
import com.ssafy.a401.artwalk_backend.domain.record.model.Record;
import com.ssafy.a401.artwalk_backend.domain.record.model.RecordImageRequestDTO;
import com.ssafy.a401.artwalk_backend.domain.record.repository.RecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordService {

	private final UserRepository userRepository;
	private final RecordRepository recordRepository;
	private final FileService fileService;

	private static String fileOption = "record";

	public Record addRecord(Record record, String userId) {
		Record result = null;

		record.setUserId(userId);

		String geometry = record.getGeometry();
		String geometryPath = fileService.saveFile(fileOption, geometry, userId);
		record.setGeometry(geometryPath);

		String thumbPath = fileService.saveThumbnail(fileOption, geometryPath, geometry, userId);
		record.setThumbnail(thumbPath);

		result = recordRepository.save(record);

		return result;
	}

	public Record modifyRecord(Record originRecord, String title) {
		Record result = null;

		originRecord.setTitle(title);
		result = recordRepository.save(originRecord);

		return result;
	}

	public long removeRecord(Record record) {
		recordRepository.delete(record);
		fileService.removeFile(fileOption, record.getGeometry(), record.getUserId());
		fileService.removeFile(fileOption, record.getThumbnail(), record.getUserId());
		fileService.removeFile(fileOption, record.getRecentImage(), record.getUserId());
		long result = recordRepository.countByRecordId(record.getRecordId());
		return result;
	}

	/** 저장된 기록의 개수를 반환합니다. */
	public long getRecordCount(String userId) {
		long count = recordRepository.countByUserId(userId);
		return count;
	}

	/** 저장된 모든 기록을 반환합니다. */
	public List<Record> findAllRecord() {
		List<Record> recordList = new ArrayList<>();
		List<Record> records = recordRepository.findAll(Sort.by(Sort.Direction.DESC, "recordId"));
		for (Record record : records) {
			record.setThumbnail(makeThumbnailUrl(record.getRecordId()));
			record.setRecentImage(makeImageUrl(record.getRecordId()));
			record.setGeometry(fileService.readFile(fileOption, record.getGeometry(), record.getUserId()));
			recordList.add(record);
		}
		return recordList;
	}

	/** 저장된 기록 중 record_id가 일치하는 기록을 반환합니다. */
	public Record findByRecordId(int recordId) {
		Optional<Record> record = recordRepository.findById(recordId);
		return record.orElse(null);
	}

	/** 저장된 기록 중 link와 record_id가 일치하는 기록을 반환합니다. */
	public Record findByLink(String link) {
		Optional<Record> record = Optional.ofNullable(recordRepository.findByLink(link));
		return record.orElse(null);
	}

	/** 저장된 기록 중 editLink와 record_id가 일치하는 기록을 반환합니다. */
	public Record findByEditLink(String editLink) {
		Optional<Record> record = Optional.ofNullable(recordRepository.findByEditLink(editLink));
		return record.orElse(null);
	}

	/** 저장된 기록 중 user_id가 일치하는 기록을 반환합니다. */
	public List<Record> findByUserId(String userId) {
		List<Record> recordList = new ArrayList<>();
		List<Record> records = recordRepository.findByUserIdOrderByRecordIdDesc(userId);
		for (Record record : records) {
			record.setThumbnail(makeThumbnailUrl(record.getRecordId()));
			record.setRecentImage(makeImageUrl(record.getRecordId()));
			record.setGeometry(fileService.readFile(fileOption, record.getGeometry(), userId));
			recordList.add(record);
		}
		return recordList;
	}

	public String readGeometryFile(Record record) {
		return fileService.readFile(fileOption, record.getGeometry(), record.getUserId());
	}

	public ResponseEntity<Resource> getThumbnailImage(Record record) {
		return fileService.findThumbnail(fileOption, record.getThumbnail(), record.getUserId());
	}

	public ResponseEntity<Resource> getShareImage(Record record) {
		if(record.getRecentImage() == null) return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
		else return fileService.findImage(fileOption, record.getRecentImage(), record.getUserId());
	}

	/** 썸네일 요청 경로를 반환합니다. */
	public static String makeThumbnailUrl(int recordId) {
		String thumbUrl = "/record/thumb/" + recordId;
		return thumbUrl;
	}

	/** 공유이미지 요청 경로를 반환합니다. */
	public static String makeImageUrl(int recordId) {
		String imageUrl = "/record/image/" + recordId;
		return imageUrl;
	}

	/** 공유이미지를 저장합니다. 만약 이미 저장되어 있는 이미지가 있는 경우 새로 만든 이미지로 교체합니다. */
	public Record saveRecordImage(Record record, RecordImageRequestDTO recordImageRequestDTO) {
		Record result = null;

		if(record.getRecentImage() != null) {
			fileService.removeFile(fileOption, record.getRecentImage(), record.getUserId());
		}
		String imagePath = fileService.saveShareImage(fileOption, record.getThumbnail(), fileService.readFile(fileOption, record.getGeometry(), record.getUserId()), recordImageRequestDTO, record.getUserId());
		record.setRecentImage(imagePath);
		record.setLink(makeRandomLink());
		result = recordRepository.save(record);

		return result;
	}

	public List<Record> findByNickNameContaining(String nickname) {
		List<Record> recordList = new ArrayList<>();
		List<Record> records = new ArrayList<>();

		List<User> userList = userRepository.findByNicknameContaining(nickname);
		for (User user : userList) {
			recordList.addAll(findByUserId(user.getUserId()));
		}
		for (Record record : recordList) {
			record.setThumbnail(makeThumbnailUrl(record.getRecordId()));
			record.setRecentImage(makeImageUrl(record.getRecordId()));
			record.setGeometry(fileService.readFile(fileOption, record.getGeometry(), record.getUserId()));
			records.add(record);
		}
		return records;
	}

	public List<Record> findByTitleContaining(String title) {
		List<Record> recordList = new ArrayList<>();
		List<Record> records = recordRepository.findByTitleContainingOrderByRecordIdDesc(title);
		for (Record record : records) {
			record.setThumbnail(makeThumbnailUrl(record.getRecordId()));
			record.setRecentImage(makeImageUrl(record.getRecordId()));
			record.setGeometry(fileService.readFile(fileOption, record.getGeometry(), record.getUserId()));
			recordList.add(record);
		}
		return recordList;
	}

	/** 새로운 편집 주소를 생성해 DB에 저장 후 반환합니다. */
	@Transactional
	public String saveRandomEditLink(Record record) {
		String randomLink = makeRandomLink();
		record.setEditLink(randomLink);
		return randomLink;
	}

	/** 새로운 공유 주소를 생성해 DB에 저장 후 반환합니다. */
	@Transactional
	public String saveRandomLink(Record record) {
		String randomLink = makeRandomLink();
		record.setEditLink(null);
		record.setLink(randomLink);
		return randomLink;
	}

	public String makeRandomLink() {
		int leftLimit = 48;
		int rightLimit = 122;
		int targetStringLength = 8;
		Random random = new Random();
		return random.ints(leftLimit, rightLimit + 1)
			.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
			.limit(targetStringLength)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}
}
