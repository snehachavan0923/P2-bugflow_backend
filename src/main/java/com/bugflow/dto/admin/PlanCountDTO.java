package com.bugflow.dto.admin;

public class PlanCountDTO {

    public String plan;
    public long count;

    public PlanCountDTO() {
    }

    public PlanCountDTO(String plan, long count) {
        this.plan = plan;
        this.count = count;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
