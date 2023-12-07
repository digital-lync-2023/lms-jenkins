package com.lms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.dto.AllCourseUsersDto;
import com.lms.dto.UserCoursesDto;
import com.lms.dto.VideoDto;
import com.lms.entity.CourseLink;
import com.lms.entity.CourseModules;
import com.lms.entity.CourseUsers;
import com.lms.entity.Courses;
import com.lms.entity.CoursesViewDto;
import com.lms.entity.CoursesViewDto.CoursesViewDtoBuilder;
import com.lms.exception.details.CustomException;
import com.lms.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class CourseController {

	@Autowired
	private CourseService cs;

	@PostMapping("/addcourseuser")
	public ResponseEntity<?> addCourseUser(@RequestParam String userName, @RequestParam String userEmail) {

		CourseUsers cu = CourseUsers.builder().username(userName).useremail(userEmail).build();

		boolean saveUserCourse = cs.saveCourseUser(cu);

		if (saveUserCourse) {
			return new ResponseEntity<String>("CourseUsers Saved", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<String>("Unable To Save CourseUsers", HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/addcourse")
	public ResponseEntity<?> addCourse(@RequestParam String courseName, @RequestParam String courseTrainer) {

		Courses cc = Courses.builder().coursename(courseName).coursetrainer(courseTrainer).build();

		boolean saveUserCourse = cs.saveCourses(cc);

		if (saveUserCourse) {
			return new ResponseEntity<String>("Courses Saved", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<String>("Unable To Save Courses", HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/accesscoursetouser")
	public ResponseEntity<?> accessCouresToUser(@RequestParam String courseUserEmail, @RequestParam String courseName,
			@RequestParam String trainerName) {
		boolean accessTocoures = cs.accessCouresToUser(courseUserEmail, courseName, trainerName);

		if (accessTocoures) {
			return new ResponseEntity<String>("Course Added To User", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Course Unable To Add User", HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping("/addvideolink")
	public ResponseEntity<String> addVideolink(@RequestBody @Valid VideoDto videoDto) {
		boolean addVideoLink = cs.addVideoLink(videoDto);

		if (addVideoLink) {
			return new ResponseEntity<>("saved", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Video Not Saved", HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping("/getcourseusers")
	public ResponseEntity<UserCoursesDto> getCourseUsers(@RequestParam String courseUserName) {

		UserCoursesDto uc = cs.getCourseUsers(courseUserName);

		if (uc == null) {
			throw new CustomException("No User Found");
		} else {
			return new ResponseEntity<UserCoursesDto>(uc, HttpStatus.OK);
		}

	}

	@GetMapping("/getcourse")
	public ResponseEntity<List<AllCourseUsersDto>> getCourses(@RequestParam String courseName, String trainerName) {

		List<AllCourseUsersDto> uc = cs.getCourses(courseName, trainerName);

		if (uc == null) {
			throw new CustomException("No User Found");
		} else {
			return new ResponseEntity<List<AllCourseUsersDto>>(uc, HttpStatus.OK);
		}

	}

	@GetMapping("/getvideos")
	public ResponseEntity<List<CoursesViewDto>> getVideoLinks(@RequestParam String userEmail,
			@RequestParam String courseName, @RequestParam String trainerName) {

		List<CourseModules> videoLink = cs.getVideoLink(userEmail, courseName, trainerName);

		List<Integer> mn = videoLink.stream().map(x -> x.getModulenum()).collect(Collectors.toList());

		List<List<CourseLink>> collect = videoLink.stream().map(x -> x.getClinks()).collect(Collectors.toList());

		List<List<CourseLink>> findFirst = collect.stream().toList();

		List<List<String>> listoflinks = findFirst.stream().flatMap(clinks -> clinks.stream().map(CourseLink::getLink))
				.collect(Collectors.toList());

		List<List<String>> listofvideonames = findFirst.stream()
				.flatMap(clinks -> clinks.stream().map(CourseLink::getVideoname)).collect(Collectors.toList());

//		Collections.reverse(listofvideonames);

		List<Map<String, String>> resultMapList = new ArrayList<>();

		for (int i = 0; i < listoflinks.size(); i++) {
			List<String> list2 = listoflinks.get(i);
			List<String> list3 = listofvideonames.get(i);

			Map<String, String> resultMap = new HashMap<>();

			for (int j = 0; j < list2.size(); j++) {

				resultMap.put(list3.get(j), list2.get(j));
			}

			resultMapList.add(resultMap);
		}

		List<CoursesViewDtoBuilder> combinedList = IntStream.range(0, Math.min(mn.size(), resultMapList.size()))
				.mapToObj(i -> CoursesViewDto.builder().modulenum(mn.get(i)).videos(resultMapList.get(i)))
				.collect(Collectors.toList());

		List<CoursesViewDto> list = combinedList.stream().map(CoursesViewDtoBuilder::build)
				.collect(Collectors.toList());

		return new ResponseEntity<List<CoursesViewDto>>(list, HttpStatus.OK);
	}
}