package com.rabo.statement.processor.writer;

import com.rabo.statement.processor.dto.XmlRecordData;
import com.rabo.statement.processor.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.rabo.statement.processor.util.FileUtil.createDirectory;

@Slf4j
@Component
public class XmlStatementFileWriter implements ItemWriter<XmlRecordData>, StepExecutionListener {

    private String inputPath;

    @Value("${spring.file.directory.error}")
    private String errorDirectory;

    @Value("${spring.file.directory.input}")
    private String inputDirectory;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        inputPath = stepExecution.getJobExecution().getJobParameters().getString(Constants.INPUT_PATH);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        List<XmlRecordData> records = (List<XmlRecordData>) stepExecution.getJobExecution().getExecutionContext().get(Constants.PREVIOUS_RECORDS);
        List<XmlRecordData> invalidRecords = (List<XmlRecordData>) stepExecution.getJobExecution().getExecutionContext().get(Constants.INVALID_RECORDS);
        List<XmlRecordData> duplicates = records.stream()
                .collect(Collectors.groupingBy(XmlRecordData::getReference))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());
        invalidRecords.addAll(duplicates);
        writeToFile(invalidRecords);
        return ExitStatus.COMPLETED;
    }

    private void writeToFile(List<XmlRecordData> totalInvalidRecords) {
        FileWriter fileWriter = null;
        try{
            String errorDirectoryPath = createDirectory(inputPath.replace(inputDirectory, errorDirectory));
            String errorFileName = errorDirectoryPath
                    .concat("/invalidXml_")
                    .concat(LocalDate.now()+"_"+System.currentTimeMillis())
                    .concat(Constants.ERROR_FILE_EXTENSION);
            fileWriter = new FileWriter(errorFileName);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            totalInvalidRecords.stream()
                    .forEach(record -> printWriter.println(record.toString()));
            printWriter.close();
            log.info("Successfully written error file");
        }catch (Exception e){
            log.error("Exception while writing error file, due to ",e.getMessage());
        }
    }

    @Override
    public void write(List<? extends XmlRecordData> list) {
        log.info("Xml Statement Writer");
    }
}
