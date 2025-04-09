package com.develop.batch.config.baseball;

import com.develop.batch.tasklet.UpdateGoodsScoreTasklet;
import com.develop.batch.tasklet.UpdateMatchResultTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class UpdateMatchResultConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UpdateMatchResultTasklet updateMatchResultTasklet;

    @Bean(name = "UPDATE_MATCH_RESULT_JOB")
    public Job UpdateGoodsScoreJob() {
        return new JobBuilder("UPDATE_MATCH_RESULT_JOB", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(updateMatchResultStep())
            .build();
    }

    @Bean(name = "UPDATE_MATCH_RESULT_STEP")
    public Step updateMatchResultStep() {
        return new StepBuilder("updateMatchResultTasklet", jobRepository)
            .tasklet(updateMatchResultTasklet, transactionManager)
            .build();
    }

}
