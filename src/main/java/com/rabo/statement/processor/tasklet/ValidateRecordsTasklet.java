package com.rabo.statement.processor.tasklet;

import com.rabo.statement.processor.dto.RecordData;
import com.rabo.statement.processor.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.rabo.statement.processor.util.FileUtil.createDirectory;

@Slf4j
@Component
public class ValidateRecordsTasklet implements Tasklet {

    @Value("${spring.file.directory.error}")
    private String errorDirectory;

    @Value("${spring.file.directory.input}")
    private String inputDirectory;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
        List<RecordData> records = (List<RecordData>) jobExecution
                .getExecutionContext()
                .get(Constants.CSV_RECORDS);
        List<RecordData> duplicates = findDuplicatesIfAny(records);
        records.removeAll(duplicates);
        List<RecordData> invalidEndBalanceList = validateEndBalance(records);
        invalidEndBalanceList.addAll(duplicates);
        writeToFile(invalidEndBalanceList, jobExecution);
        return RepeatStatus.FINISHED;
    }

    private List<RecordData> validateEndBalance(List<RecordData> records) {
        List<RecordData> invalidData = records.stream()
                .filter(e -> e.getEndBalance().compareTo(BigDecimal.ZERO) < Constants.ZERO)
                .collect(Collectors.toList());
        return invalidData;
    }

    private List<RecordData> findDuplicatesIfAny(List<RecordData> records) {
        List<RecordData> duplicates = records.stream()
                .collect(Collectors.groupingBy(RecordData::getReference))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());
        return duplicates;
    }

    private void writeToFile(List<RecordData> totalInvalidRecords, JobExecution jobExecution) {
        FileWriter fileWriter = null;
        try{
            String inputPath = jobExecution.getJobParameters().getString(Constants.INPUT_PATH);
            String errorDirectoryPath = createDirectory(inputPath.replace(inputDirectory, errorDirectory));
            String errorFileName = errorDirectoryPath
                    .concat("/invalidCsv_")
                    .concat(LocalDate.now()+"_"+System.currentTimeMillis())
                    .concat(Constants.ERROR_FILE_EXTENSION);
            fileWriter = new FileWriter(errorFileName);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            totalInvalidRecords.stream()
                    .forEach(record -> printWriter.println(record.toString()));
            printWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
