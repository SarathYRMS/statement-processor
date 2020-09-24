package com.rabo.statement.processor.launcher;

import com.rabo.statement.processor.util.Constants;
import com.rabo.statement.processor.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class StatementProcessJobLauncher implements SchedulingConfigurer {

    @Autowired
    @Qualifier("csvStatementTaskletProcessorJob")
    private Job csvStatementTaskletProcessor;

    @Autowired
    @Qualifier("xmlStatementChunkProcessorJob")
    private Job xmlStatementChunkProcessor;

    @Autowired
    private JobLauncher jobLauncher;

    @Value("${spring.cron.csvscheduler.value}")
    private String schedulerValue;

    @Value("${spring.cron.xmlscheduler.value}")
    private String xmlSchedulerValue;

    @Value("${spring.file.directory.current}")
    private String currentInputDirectory;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(taskExecutor());

        List<File> files = FileUtil.readFilesFromDirectory(currentInputDirectory);
        if(files.isEmpty()) {
            log.warn("No files found, hence not processing");
        }else{
            files.forEach(file -> {
                String extension = FilenameUtils.getExtension(file.getName());
                switch (extension) {
                    case Constants.CSV:
                        scheduledTaskRegistrar.addCronTask(() -> {
                            log.info("CSV Tasklet Batch job is starting");
                            launchJob(csvStatementTaskletProcessor, file);
                        }, schedulerValue);
                        break;
                    case Constants.XML:
                        scheduledTaskRegistrar.addCronTask(() -> {
                            log.info("XML Chunk Batch job is starting");
                            launchJob(xmlStatementChunkProcessor, file);
                        }, xmlSchedulerValue);
                        break;
                    default:
                        log.warn("No intended file type found, hence not processing");
                }
            });
        }
    }

    public void launchJob(Job job, File file){
        JobParameters params = new JobParametersBuilder()
                .addString(Constants.JOB_ID, String.valueOf(System.currentTimeMillis()))
                .addString(Constants.INPUT_FILE, file.getPath())
                .addString(Constants.INPUT_PATH, file.getParent())
                .toJobParameters();
        try{
            log.info("Job has started from Launcher");
            JobExecution jobExecution = jobLauncher.run(job,params);
            log.info("job completed with status/exit code ", jobExecution.getExitStatus().getExitCode());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Executor taskExecutor() {
        return Executors.newScheduledThreadPool(2);
    }
}