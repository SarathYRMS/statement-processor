package com.rabo.statement.processor.tasklet;

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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class VerifyInputFileTaskletTest {

    @Mock
    private ChunkContext chunkContext;
    @Mock
    private StepContribution stepContribution;
    @Mock
    private StepContext stepContext;
    @Mock
    private StepExecution stepExecution;
    @Mock
    private JobExecution jobExecution;
    @Mock
    private JobParameters jobParameters;
    @InjectMocks
    private VerifyInputFileTasklet verifyInputFileTasklet;

    @Before
    public void setUp() throws IOException {
        verifyInputFileTasklet = new VerifyInputFileTasklet();

        Resource resource = new ClassPathResource("input/records.csv");
        String inputFile = resource.getFile().getAbsolutePath();

        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getStepExecution()).thenReturn(stepExecution);
        when(stepExecution.getJobExecution()).thenReturn(jobExecution);
        when(jobExecution.getJobParameters()).thenReturn(jobParameters);
        when(jobExecution.getJobParameters().getString("inputFile")).thenReturn(inputFile);
    }

    @Test
    public void testExecute(){
        RepeatStatus status = verifyInputFileTasklet.execute(stepContribution, chunkContext);
        Assert.assertNotNull(status);
        Assert.assertEquals(RepeatStatus.FINISHED, status);
    }

    @Test
    public void testExecuteWithEmptyFile() throws IOException {
        Resource resource = new ClassPathResource("input/empty.csv");
        String inputFile = resource.getFile().getAbsolutePath();
        when(jobExecution.getJobParameters().getString("inputFile")).thenReturn(inputFile);
        RepeatStatus status = verifyInputFileTasklet.execute(stepContribution, chunkContext);
        Assert.assertNotNull(status);
        Assert.assertEquals(RepeatStatus.FINISHED, status);
    }

    @Test
    public void testExecuteWithoutFile() throws IOException {
        Resource resource = new ClassPathResource("input/");
        String inputFile = resource.getFile().getAbsolutePath().concat("/none.csv");
        when(jobExecution.getJobParameters().getString("inputFile")).thenReturn(inputFile);
        RepeatStatus status = verifyInputFileTasklet.execute(stepContribution, chunkContext);
        Assert.assertNotNull(status);
        Assert.assertEquals(RepeatStatus.FINISHED, status);
    }
}