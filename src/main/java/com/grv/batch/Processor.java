package com.grv.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.job.flow.FlowExecutionException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.grv.model.User;

//Processor for the step
@Component
public class Processor implements ItemProcessor<User, User> {

	private static final Map<String, String> DEPT_NAMES = new HashMap<String, String>();
	
	private static int count;
	
	public Processor() {
		count = 0;
		DEPT_NAMES.put("001", "Technology");
		DEPT_NAMES.put("002", "Operation");
		DEPT_NAMES.put("003", "Accounts");
	}
	
	@Override
	public User process(User user) throws Exception {
		String deptCode = user.getDept();
		String dept = DEPT_NAMES.get(deptCode);
		user.setDept(dept);
		
		user.setTimestamp(new Date());
		
		if(count==0) {
			count++;
			throw new FlowExecutionException("Exception for testing...");
		}
		
		System.out.println(String.format("Converted from [%s] to [%s]", deptCode, dept));
		return user;
	}

}
