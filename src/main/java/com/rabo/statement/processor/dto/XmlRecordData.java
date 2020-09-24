package com.rabo.statement.processor.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@Data
@XmlRootElement(name = "record")
public class XmlRecordData {

    private String reference;
    private String accountNumber;
    private String description;
    private BigDecimal startBalance;
    private String mutation;
    private BigDecimal endBalance;

    @XmlAttribute(name = "reference")
    public String getReference() {
        return reference;
    }

    @Override
    public String toString() {
        return "Reference Number='" + reference + '\'' +
                ", Description='" + description + '\'';
    }
}
