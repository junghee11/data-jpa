package com.develop.batch.tasklet;

import com.develop.datajpa.repository.baseball.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExampleTasklet implements Tasklet {

    private final TeamRepository teamRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        LocalDateTime startTime = contribution.getStepExecution().getJobExecution().getJobParameters().getLocalDateTime("date");
        log.info("start at {}", startTime);
        teamRepository.findAll();

        return RepeatStatus.FINISHED;
    }

}
