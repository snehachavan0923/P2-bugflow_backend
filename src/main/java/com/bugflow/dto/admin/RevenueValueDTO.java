package com.bugflow.dto.admin;

import java.math.BigDecimal;

public class RevenueValueDTO {

    public String label;
    public BigDecimal value;

    public RevenueValueDTO() {
    }

    public RevenueValueDTO(String label, BigDecimal value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
