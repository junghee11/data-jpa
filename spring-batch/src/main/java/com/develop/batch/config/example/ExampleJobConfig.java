package com.develop.batch.config.example;

import com.develop.batch.tasklet.ExampleTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ExampleJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ExampleTasklet exampleTasklet;

    @Bean(name = "EXAMPLE_JOB")
    public Job ExampleJob() {
        return new JobBuilder("EXAMPLE_JOB", jobRepository)
            .start(exampleStep())
            .build();
    }

    @Bean(name = "EXAMPLE_STEP")
    public Step exampleStep() {
        return new StepBuilder("EXAMPLE_STEP", jobRepository)
            .tasklet(exampleTasklet, transactionManager)
            .allowStartIfComplete(true)
            .build();
    }

}
