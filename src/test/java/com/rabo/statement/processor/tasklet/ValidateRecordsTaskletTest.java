package com.rabo.statement.processor.tasklet;

import com.rabo.statement.processor.dto.RecordData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ValidateRecordsTaskletTest {


    @Mock
    private ChunkContext chunkContext;
    @Mock
    private StepContribution stepContribution;
    @Mock
    private JobExecution jobExecution;
    @Mock
    private ExecutionContext executionContext;
    @Mock
    private StepExecution stepExecution;
    @Mock
    private StepContext stepContext;
    @Mock
    private JobParameters jobParameters;
    @InjectMocks
    private ValidateRecordsTasklet validateRecordsTasklet;

    @Before
    public void setUp(){
        validateRecordsTasklet = new ValidateRecordsTasklet();

        List<RecordData> recordDataList = new ArrayList<>();
        RecordData record1 = createMockRecord("156108", new BigDecimal(29), "Flowers from Erik de Vries");
        RecordData record2 = createMockRecord("156108", new BigDecimal(55.82), "Clothes from Dani�l Bakker");
        recordDataList.add(record1);
        recordDataList.add(record2);

        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getStepExecution()).thenReturn(stepExecution);
        when(stepExecution.getJobExecution()).thenReturn(jobExecution);
        when(jobExecution.getExecutionContext()).thenReturn(executionContext);
        when(executionContext.get("records")).thenReturn(recordDataList);
        when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    }

    @Test
    public void testExecute(){
        RepeatStatus status = validateRecordsTasklet.execute(stepContribution, chunkContext);
        Assert.assertNotNull(status);
        Assert.assertEquals(RepeatStatus.FINISHED, status);
    }

    private RecordData createMockRecord(String refNum, BigDecimal balance, String description) {
        RecordData record = new RecordData();
        record.setReference(refNum);
        record.setEndBalance(balance);
        record.setDescription(description);
        return record;
    }
}