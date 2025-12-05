package com.example.config;

import com.example.model.StudentJdbc;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

@Configuration
public class SampleJob {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Bean
	public Job chunkJob() {
		return new JobBuilder("Chunk Job", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(firstChunkStep())
				.build();
	}

    @Bean
	public Step firstChunkStep() {
		return new StepBuilder("First Chunk Step", jobRepository)
				.<StudentJdbc, StudentJdbc>chunk(100, transactionManager)
				.reader(jdbcCursorItemReader())
				.writer(flatFileItemWriter())
				.build();
	}

    @Bean
	public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
		JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader = new JdbcCursorItemReader<>();
		jdbcCursorItemReader.setDataSource(dataSource);
		jdbcCursorItemReader.setSql("select id, first_name as firstName, last_name as lastName,email from students");

		jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<>() {
            {
                setMappedClass(StudentJdbc.class);
            }
        });
		return jdbcCursorItemReader;
	}
	
	@Bean
	public FlatFileItemWriter<StudentJdbc> flatFileItemWriter() {
		FlatFileItemWriter<StudentJdbc> flatFileItemWriter = new FlatFileItemWriter<StudentJdbc>();
		flatFileItemWriter.setResource(new FileSystemResource(new File("./outputFiles/students.csv")));
		
		flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.write("Id,First Name,Last Name,Email");
			}
		});
		
		flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<>() {
            {
                setFieldExtractor(new BeanWrapperFieldExtractor<>() {
                    {
                        setNames(new String[]{"id", "firstName", "lastName", "email"});
                    }
                });
            }
        });
		
		flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
			@Override
			public void writeFooter(Writer writer) throws IOException {
				writer.write("Created @ " + new Date());
			}
		});
		
		return flatFileItemWriter;
	}
}
