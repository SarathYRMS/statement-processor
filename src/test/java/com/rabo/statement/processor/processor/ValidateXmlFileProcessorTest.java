package com.rabo.statement.processor.processor;

import com.rabo.statement.processor.dto.XmlRecordData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

public class ValidateXmlFileProcessorTest {

    @MockBean
    private XmlRecordData xmlRecordData;
    @InjectMocks
    private ValidateXmlFileProcessor processor = new ValidateXmlFileProcessor();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessWithValidEndBalance() {
        xmlRecordData = createMockRecord("189177", new BigDecimal(4), "Subscription for Erik Dekker");
        XmlRecordData processedRecord = processor.process(xmlRecordData);
        Assert.assertEquals("189177", processedRecord.getReference());
        Assert.assertEquals(new BigDecimal(4), processedRecord.getEndBalance());
        Assert.assertEquals("Subscription for Erik Dekker", processedRecord.getDescription());
    }

    @Test
    public void testProcessWithInvalidEndBalance() {
        xmlRecordData = createMockRecord("193141", new BigDecimal(-34.2), "Clothes from Daniël Theuß");
        XmlRecordData processedRecord = processor.process(xmlRecordData);
        Assert.assertTrue(processedRecord!=null);
        Assert.assertEquals("193141", processedRecord.getReference());
        Assert.assertEquals(new BigDecimal(-34.2), processedRecord.getEndBalance());
        Assert.assertEquals("Clothes from Daniël Theuß", processedRecord.getDescription());
    }

    private XmlRecordData createMockRecord(String refNum, BigDecimal balance, String description) {
        XmlRecordData record = new XmlRecordData();
        record.setReference(refNum);
        record.setEndBalance(balance);
        record.setDescription(description);
        return record;
    }
}