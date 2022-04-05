package com.grv.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.grv.listeners.StepListener;
import com.grv.model.User;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
	
	@Autowired
	private StepListener stepListener;
	
	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
			ItemReader<User> itemReader, ItemReader<User> itemReader1, ItemProcessor<User, User> itemProcessor, ItemWriter<User> itemWriter) {
		Step step1 = step1(stepBuilderFactory, itemReader, itemProcessor, itemWriter);
		Step step2 = step2(stepBuilderFactory, itemReader1, itemProcessor, itemWriter);

		Job job = jobBuilderFactory.get("ETL-Load")
				.incrementer(new RunIdIncrementer())	//incrementer - sequesnce of id which we assign to every run
				.start(step1).on("FAILED").end()
				.next(step2).on("FAILED").end().build()
				.build();
		
		return job;
	}

	private TaskletStep step1(StepBuilderFactory stepBuilderFactory, ItemReader<User> itemReader,
			ItemProcessor<User, User> itemProcessor, ItemWriter<User> itemWriter) {
		return stepBuilderFactory.get("ETL-File-Load-step1")
				.<User, User>chunk(100)
				.reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.faultTolerant().retryLimit(3).retry(Exception.class)
				.listener(stepListener)
				.build();
	}
	
	private TaskletStep step2(StepBuilderFactory stepBuilderFactory, ItemReader<User> itemReader,
			ItemProcessor<User, User> itemProcessor, ItemWriter<User> itemWriter) {
		return stepBuilderFactory.get("ETL-File-Load-step2")
				.<User, User>chunk(100)
				.reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter)
				.faultTolerant().retryLimit(3).retry(Exception.class)
				.listener(stepListener)
				.build();
	}
	
	//function to create reader bean
	@Bean
	public FlatFileItemReader<User> itemReader(@Value("${input}") ClassPathResource resource){
		FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<User>();
		itemReaderConfig(resource, flatFileItemReader);
		return flatFileItemReader;
	}

	private void itemReaderConfig(ClassPathResource resource, FlatFileItemReader<User> flatFileItemReader) {
		flatFileItemReader.setResource(resource);
		flatFileItemReader.setName("CSV-Reader");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
	}
	
	@Bean
	public FlatFileItemReader<User> itemReader1(@Value("${input2}") ClassPathResource resource){
		FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<User>();
		itemReaderConfig(resource, flatFileItemReader);
		return flatFileItemReader;
	}

	private LineMapper<User> lineMapper() {
		DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<User>();
		
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");//as csv file is comma seperated
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames(new String[] {"id", "name", "dept", "salary"});//set names with respect to csv file headers
		
		BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<User>();
		fieldSetMapper.setTargetType(User.class);
		
		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		
		return defaultLineMapper;
	}
	

}
