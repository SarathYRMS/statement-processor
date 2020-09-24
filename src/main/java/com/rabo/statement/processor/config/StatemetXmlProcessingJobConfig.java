package com.rabo.statement.processor.config;

import com.rabo.statement.processor.dto.XmlRecordData;
import com.rabo.statement.processor.listener.JobCompletionNotificationListener;
import com.rabo.statement.processor.processor.ValidateXmlFileProcessor;
import com.rabo.statement.processor.reader.XmlStatementFileReader;
import com.rabo.statement.processor.tasklet.StepExecutionDecider;
import com.rabo.statement.processor.tasklet.VerifyInputFileTasklet;
import com.rabo.statement.processor.util.Constants;
import com.rabo.statement.processor.writer.XmlStatementFileWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StatemetXmlProcessingJobConfig {

    @Autowired
    private XmlStatementFileReader xmlStatementFileReader;
    @Autowired
    private ValidateXmlFileProcessor validateXmlFileProcessor;
    @Autowired
    private XmlStatementFileWriter xmlStatementFileWriter;
    @Autowired
    private VerifyInputFileTasklet verifyInputFileTasklet;
    @Autowired
    private StepExecutionDecider decider;

    @Value("${spring.xml.chunk.size}")
    private int chunkSize;

    @Bean
    public Job xmlStatementChunkProcessorJob(JobBuilderFactory jobBuilderFactory,
                                             StepBuilderFactory stepBuilderFactory) {
        return jobBuilderFactory
                .get(Constants.XML_PROCESSOR_JOB)
                .incrementer(new RunIdIncrementer())
                .start(verifyXmlInputFileTasklet(stepBuilderFactory))
                .next(decider).on(Constants.COMPLETED)
                .to(processXmlDataStatement(stepBuilderFactory))
                .end()
                .listener(getListener())
                .build();
    }

    @Bean
    public Step verifyXmlInputFileTasklet(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory
                .get(Constants.VERIFY_XML_INPUT_FILE_TASKLET)
                .tasklet(verifyInputFileTasklet)
                .build();
    }

    @Bean
    public Step processXmlDataStatement(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory
                .get(Constants.PROCESS_XML_DATA_STATEMENT)
                .listener(getListener())
                .<XmlRecordData, XmlRecordData>chunk(chunkSize)
                .reader(xmlStatementFileReader.read())
                .processor(validateXmlFileProcessor)
                .writer(xmlStatementFileWriter)
                .build();
    }

    @Bean(name = Constants.XML_JOB_LISTENER)
    public JobCompletionNotificationListener getListener(){
        return new JobCompletionNotificationListener();
    }
}
