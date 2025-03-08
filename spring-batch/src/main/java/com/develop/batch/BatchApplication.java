package com.develop.batch;

import com.develop.batch.runner.JobRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.develop")
public class BatchApplication implements CommandLineRunner {

    private final JobRunner jobRunner;

    public BatchApplication(JobRunner jobRunner) {
        this.jobRunner = jobRunner;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(BatchApplication.class, args)));
    }

    @Override
    public void run(String... args) throws Exception {
        jobRunner.runJob();
    }
}
