package com.rabo.statement.processor.dto;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordData {

    @CsvBindByPosition(position = 0, required = true)
    private String reference;
    @CsvBindByPosition(position = 1, required = true)
    private String accountNumber;
    @CsvBindByPosition(position = 2, required = true)
    private String description;
    @CsvBindByPosition(position = 3, required = true)
    private BigDecimal startBalance;
    @CsvBindByPosition(position = 4, required = true)
    private String mutation;
    @CsvBindByPosition(position = 5, required = true)
    private BigDecimal endBalance;

    @Override
    public String toString() {
        return "Reference Number='" + reference + '\'' +
                ", Description='" + description + '\'';
    }
}
