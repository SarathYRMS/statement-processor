package com.rabo.statement.processor.tasklet;

import com.rabo.statement.processor.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
public class VerifyInputFileTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        String inputFile = stepExecution.getJobExecution().getJobParameters().getString(Constants.INPUT_FILE);
        if(!Files.exists(Paths.get((inputFile)))){
            stepExecution.addFailureException(new FileNotFoundException("Input file not available, hence not proceeding further"));
            log.info("No input file, so not processing");
            stepContribution.setExitStatus(ExitStatus.FAILED);
        } else if(new File(inputFile).length() == Constants.ZERO){
            stepExecution.addFailureException(new FileNotFoundException("Input file is empty, hence not proceeding further"));
            log.info("Input file is empty, so not processing");
            stepContribution.setExitStatus(ExitStatus.FAILED);
        }
        return RepeatStatus.FINISHED;
    }
}
