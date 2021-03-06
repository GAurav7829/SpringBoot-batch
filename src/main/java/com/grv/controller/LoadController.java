package com.grv.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/load")
public class LoadController {
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private Job job; //created job in SpringBatchConfig file
	
	@GetMapping
	public BatchStatus load() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		Map<String, JobParameter> maps = new HashMap<String, JobParameter>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(job, parameters);//jobExecution - weather the job is executed
		System.out.println("JobExecution: "+jobExecution.getStatus());
		System.out.println("Batch is Running");
		while(jobExecution.isRunning()) {
			System.out.println("...");
		}
		return jobExecution.getStatus();
	}
	
	@Autowired
	@Qualifier("job1")
	private Job job1;
	
	@GetMapping("/job1")
	public BatchStatus executeJobWithTasklet() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		Map<String, JobParameter> maps = new HashMap<String, JobParameter>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(maps);
		JobExecution jobExecution = jobLauncher.run(job1, parameters);
		System.out.println("JobExecution: "+jobExecution.getStatus());
		System.out.println("Batch is Running");
		while(jobExecution.isRunning()) {
			System.out.println("...");
		}
		return jobExecution.getStatus();
	}
}
