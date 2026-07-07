package com.bugflow.dto;

public class WeeklyDataDTO {
    private String week;
    private int count;

    public WeeklyDataDTO(String week, int count) {
        this.week = week;
        this.count = count;
    }

    public WeeklyDataDTO() {
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
