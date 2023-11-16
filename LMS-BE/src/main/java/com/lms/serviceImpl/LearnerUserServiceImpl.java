package com.lms.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.lms.entity.LearnerUser;
import com.lms.exception.details.EmailNotFoundException;
import com.lms.repository.LearnerUserRepo;
import com.lms.service.LearnerUserService;

@Service
public class LearnerUserServiceImpl implements LearnerUserService {

	@Autowired
	private LearnerUserRepo lur;

	@Override
	public ResponseEntity<LearnerUser> saveLU(LearnerUser lu) {

		return new ResponseEntity<LearnerUser>(lur.save(lu), HttpStatus.OK);

	}

	@Override
	public Boolean getByemail(LearnerUser lu) {

		boolean findByName = lur.existsByemail(lu.getEmail());
		return findByName;
	}

	@Override
	public List<LearnerUser> getLU(long id) {
		return null;
	}

	@Override
	public LearnerUser updateLU(LearnerUser lu) {
		return null;
	}

	@Override
	public void deleteLU(long id) {

	}

	@Override
	public ResponseEntity<?> getby(LearnerUser lu) {

		if (lur.findByemail(lu.getEmail()).isEmpty()) {

			throw new EmailNotFoundException();
		} else {

			return new ResponseEntity<Object>(lur.findByemail(lu.getEmail()).get().getName(), HttpStatus.OK);
		}

	}

}
