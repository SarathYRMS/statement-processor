package com.rabo.statement.processor.tasklet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileReadingTaskletTest {

    @Mock
    private ChunkContext chunkContext;
    @Mock
    private StepContribution stepContribution;
    @Mock
    private StepContext stepContext;
    @InjectMocks
    private FileReadingTasklet fileReadingTasklet;

    @Before
    public void setUp() throws IOException {
        fileReadingTasklet = new FileReadingTasklet();

        Resource resource = new ClassPathResource("input/records.csv");
        String inputFile = resource.getFile().getAbsolutePath();

        Map<String, Object> jobParams = new HashMap<>();
        jobParams.put("inputFile", inputFile);

        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getJobParameters()).thenReturn(jobParams);
        when(chunkContext.getStepContext().getJobParameters().get("inputFile")).thenReturn(jobParams);
    }

    @Test
    public void testExecute() {
        RepeatStatus status = fileReadingTasklet.execute(stepContribution, chunkContext);
        Assert.assertNotNull(status);
        Assert.assertEquals(RepeatStatus.FINISHED, status);
    }
}