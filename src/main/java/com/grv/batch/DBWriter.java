package com.grv.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.grv.model.User;
import com.grv.repository.UserRepository;

//create item writer for step
@Component
public class DBWriter implements ItemWriter<User> {

	@Autowired
	private UserRepository userRepository; 
	
	@Override
	public void write(List<? extends User> users) throws Exception {
		System.out.println("Data saved for Users: "+ users);
		userRepository.saveAll(users);
	}

}
