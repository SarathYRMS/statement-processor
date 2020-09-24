package com.rabo.statement.processor.config;

import com.rabo.statement.processor.listener.JobCompletionNotificationListener;
import com.rabo.statement.processor.tasklet.FileReadingTasklet;
import com.rabo.statement.processor.tasklet.StepExecutionDecider;
import com.rabo.statement.processor.tasklet.ValidateRecordsTasklet;
import com.rabo.statement.processor.tasklet.VerifyInputFileTasklet;
import com.rabo.statement.processor.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StatementCsvProcessingJobConfig {

    @Autowired
    private FileReadingTasklet fileReadingTasklet;
    @Autowired
    private ValidateRecordsTasklet validateRecordsTasklet;
    @Autowired
    private VerifyInputFileTasklet verifyInputFileTasklet;
    @Autowired
    private StepExecutionDecider decider;

    @Bean
    public Job csvStatementTaskletProcessorJob(JobBuilderFactory jobBuilderFactory,
                                               StepBuilderFactory stepBuilderFactory) {
        return jobBuilderFactory
                .get(Constants.CSV_PROCESSOR_JOB)
                .incrementer(new RunIdIncrementer())
                .listener(getListener())
                .start(verifyCsvInputFileTasklet(stepBuilderFactory))
                .next(decider).on(Constants.COMPLETED)
                .to(processStatementsInTasklet(stepBuilderFactory))
                .next(validateRecords(stepBuilderFactory))
                .end()
                .build();
    }

    @Bean
    public Step verifyCsvInputFileTasklet(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory
                .get(Constants.VERIFY_CSV_INPUT_FILE_TASKLET)
                .tasklet(verifyInputFileTasklet)
                .build();
    }

    @Bean
    public Step processStatementsInTasklet(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory
                .get(Constants.PROCESS_STATEMENT_IN_TASKLET)
                .tasklet(fileReadingTasklet)
                .build();
    }

    @Bean
    public Step validateRecords(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory
                .get(Constants.VALIDATE_RECORDS)
                .tasklet(validateRecordsTasklet)
                .build();
    }

    @Bean(name = Constants.CVS_JOB_LISTENER)
    public JobCompletionNotificationListener getListener() {
        return new JobCompletionNotificationListener();
    }
}

