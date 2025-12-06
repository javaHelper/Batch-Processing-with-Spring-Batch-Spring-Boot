package com.example.config;

import com.example.model.StudentJdbc;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class SampleJob {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public Job chunkJob() {
		return new JobBuilder("Chunk Job", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(firstChunkStep())
				.build();
	}
	
	private Step firstChunkStep() {
		return new StepBuilder("First Chunk Step", jobRepository)
				.<StudentJdbc, StudentJdbc>chunk(100, transactionManager)
				.reader(jdbcCursorItemReader())
				.writer(staxEventItemWriter())
				.build();
	}
	
	public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
		JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader = new JdbcCursorItemReader<StudentJdbc>();
		jdbcCursorItemReader.setDataSource(dataSource);
		jdbcCursorItemReader.setSql("select id, first_name as firstName, last_name as lastName,email from students");

		jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>() {
			{
				setMappedClass(StudentJdbc.class);
			}
		});
		return jdbcCursorItemReader;
	}
	
	@Bean
	public JsonFileItemWriter<StudentJdbc> flatFileItemWriter() {
		FileSystemResource fileSystemResource = new FileSystemResource(new File("outputFiles/students.json"));

        return new JsonFileItemWriter<>(fileSystemResource, new JacksonJsonObjectMarshaller<>());
	}
	
	@Bean
	public StaxEventItemWriter<StudentJdbc> staxEventItemWriter() {
		FileSystemResource fileSystemResource = new FileSystemResource(new File("outputFiles/students.xml"));

		StaxEventItemWriter<StudentJdbc> staxEventItemWriter = new StaxEventItemWriter<StudentJdbc>();

		staxEventItemWriter.setResource(fileSystemResource);
		staxEventItemWriter.setRootTagName("students");

		staxEventItemWriter.setMarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentJdbc.class);
			}
		});

		return staxEventItemWriter;
	}
}
