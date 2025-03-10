package com.develop.batch.config.review;

import com.develop.batch.tasklet.UpdateGoodsScoreTasklet;
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
public class UpdateGoodsScoreJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UpdateGoodsScoreTasklet updateGoodsScoreTasklet;

    @Bean(name = "UPDATE_GOODS_SCORE_JOB")
    public Job UpdateGoodsScoreJob() {
        return new JobBuilder("UPDATE_GOODS_SCORE_JOB", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(updateGoodsScoreStep())
            .build();
    }

    @Bean(name = "UPDATE_GOODS_SCORE_STEP")
    public Step updateGoodsScoreStep() {
        return new StepBuilder("UpdateGoodsScoreStep", jobRepository)
            .tasklet(updateGoodsScoreTasklet, transactionManager)
            .build();
    }

}
