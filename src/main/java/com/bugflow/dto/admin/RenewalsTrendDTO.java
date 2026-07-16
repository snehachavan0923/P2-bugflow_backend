package com.bugflow.dto.admin;

public class RenewalsTrendDTO {

    public String date;
    public long renewals;

    public RenewalsTrendDTO() {
    }

    public RenewalsTrendDTO(String date, long renewals) {
        this.date = date;
        this.renewals = renewals;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getRenewals() {
        return renewals;
    }

    public void setRenewals(long renewals) {
        this.renewals = renewals;
    }
}
