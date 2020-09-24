package com.rabo.statement.processor.processor;

import com.rabo.statement.processor.dto.XmlRecordData;
import com.rabo.statement.processor.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ValidateXmlFileProcessor implements ItemProcessor<XmlRecordData, XmlRecordData>, StepExecutionListener {

    private List<XmlRecordData> previousRecords = new ArrayList<>();
    private List<XmlRecordData> invalidRecords = new ArrayList<>();

    private JobExecution jobExecution;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        jobExecution = stepExecution.getJobExecution();
    }

    @Override
    public XmlRecordData process(XmlRecordData xmlRecordData) {
        if(xmlRecordData.getEndBalance().compareTo(BigDecimal.ZERO) < Constants.ZERO){
            this.invalidRecords.add(xmlRecordData);
        }
        this.previousRecords.add(xmlRecordData);
        return xmlRecordData;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        jobExecution.getExecutionContext().put(Constants.INVALID_RECORDS, this.invalidRecords);
        jobExecution.getExecutionContext().put(Constants.PREVIOUS_RECORDS, this.previousRecords);
        return ExitStatus.COMPLETED;
    }
}
