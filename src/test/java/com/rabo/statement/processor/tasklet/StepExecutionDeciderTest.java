package com.rabo.statement.processor.tasklet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class StepExecutionDeciderTest {

    @Mock
    private JobExecution jobExecution;
    @Mock
    private StepExecution stepExecution;
    @InjectMocks
    private StepExecutionDecider stepExecutionDecider;

    @Before
    public void setUp(){
        stepExecutionDecider = new StepExecutionDecider();
        when(stepExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);
    }

    @Test
    public void testDecideCompletedFlow(){
        FlowExecutionStatus flowStatus = stepExecutionDecider.decide(jobExecution, stepExecution);
        Assert.assertNotNull(flowStatus);
        Assert.assertEquals(FlowExecutionStatus.COMPLETED, flowStatus);
    }

    @Test
    public void testDecideStoppedFlow(){
        when(stepExecution.getExitStatus()).thenReturn(ExitStatus.STOPPED);
        FlowExecutionStatus flowStatus = stepExecutionDecider.decide(jobExecution, stepExecution);
        Assert.assertNotNull(flowStatus);
        Assert.assertEquals(FlowExecutionStatus.STOPPED, flowStatus);
    }
}