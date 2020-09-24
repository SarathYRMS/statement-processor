package com.rabo.statement.processor.listener;

import com.rabo.statement.processor.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import static com.rabo.statement.processor.util.FileUtil.createDirectory;

@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Value("${spring.file.directory.processed}")
    private String processedDirectory;

    @Value("${spring.file.directory.input}")
    private String inputDirectory;

    @Override
    public void afterJob(JobExecution jobExecution){
        log.info("JobCompletionNotificationListener ExitStatus: "+jobExecution.getExitStatus());
        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            log.info("Job completed!!");
            String jobName = jobExecution.getJobInstance().getJobName();
            String inputFile = jobExecution.getJobParameters().getString(Constants.INPUT_FILE);
            String processedDirectoryPath = createDirectory(jobExecution.getJobParameters().getString(Constants.INPUT_PATH).replace(inputDirectory, processedDirectory));
            //move input file to processed folder
            try{
                LocalDate now = LocalDate.now();
                String processedFileName =processedDirectoryPath.concat("/processed_").concat(now+"_"+System.currentTimeMillis());
                if(Constants.XML_PROCESSOR_JOB.equals(jobName)){
                    Files.move(Paths.get(inputFile),
                            Paths.get(processedFileName.concat(Constants.XML_EXTENSION)));
                } else if(Constants.CSV_PROCESSOR_JOB.equals(jobName)){
                    Files.move(Paths.get(inputFile),
                            Paths.get(processedFileName.concat(Constants.CSV_EXTENSION)));
                }
            }catch (IOException e){
                log.error("Unable to move files from input to backup folder ", e.getMessage());
            }
        } else if(jobExecution.getStatus() == BatchStatus.FAILED){
            log.error("Job failed ", jobExecution.getAllFailureExceptions());
        }
    }
}
