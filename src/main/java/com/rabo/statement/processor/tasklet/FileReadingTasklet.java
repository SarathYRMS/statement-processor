package com.rabo.statement.processor.tasklet;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.rabo.statement.processor.dto.RecordData;
import com.rabo.statement.processor.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FileReadingTasklet implements Tasklet, StepExecutionListener {

    private List<RecordData> recordDataList = new ArrayList<>();

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        String input = (String) chunkContext.getStepContext().getJobParameters().get(Constants.INPUT_FILE);
        try {
            File inputFile = new File(input);
            InputStream inputStream = new FileInputStream(inputFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            this.recordDataList = parseCSVData(reader);
        }catch (Exception e){
            e.printStackTrace();
        }
        return RepeatStatus.FINISHED;
    }

    private List<RecordData> parseCSVData(BufferedReader reader) {
        CsvToBean<RecordData> csvToBean = new CsvToBeanBuilder<RecordData>(reader)
                .withSeparator(',')
                .withSkipLines(1)
                .withType(RecordData.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        return csvToBean.parse();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution
                .getJobExecution()
                .getExecutionContext()
                .put(Constants.CSV_RECORDS, this.recordDataList);
        return stepExecution.getExitStatus();
    }
}
