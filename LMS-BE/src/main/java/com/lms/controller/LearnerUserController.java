package com.lms.controller;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.entity.LearnerUser;

@RestController
@RequestMapping("/user")
public class LearnerUserController {

	@Autowired
	private DataSource ds;

	@Autowired
	private Environment environment;

	@PostMapping("/save")
	public LearnerUser save(@RequestBody LearnerUser lu) {

		return null;

	}

	@GetMapping("/testdb")
	public String test() throws SQLException {

		String dbUsername = environment.getProperty("spring.datasource.username");
		String dbPassword = environment.getProperty("spring.datasource.password");

		Connection connection = ds.getConnection();
		Connection connection2 = ds.getConnection(dbUsername, dbPassword);
		
		
		if (connection2 != null) {
			return "Api Is Connected " + dbUsername;
		} else {
			return "Api Not Working";
		}

	}

}