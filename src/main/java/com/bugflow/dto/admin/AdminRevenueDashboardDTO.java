package com.bugflow.dto.admin;

import java.math.BigDecimal;
import java.util.List;

public class AdminRevenueDashboardDTO {

    public BigDecimal totalRevenue;
    public BigDecimal monthlyRevenue;
    public BigDecimal todayRevenue;
    public long activeSubscriptions;
    public long expiredSubscriptions;
    public long renewalsToday;
    public long renewalsThisWeek;
    public List<RevenueValueDTO> monthlyRevenueChart;
    public List<RevenueValueDTO> revenueByPlan;
    public List<PlanCountDTO> planDistribution;
    public List<RenewalsTrendDTO> renewalsTrend;

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public long getActiveSubscriptions() {
        return activeSubscriptions;
    }

    public void setActiveSubscriptions(long activeSubscriptions) {
        this.activeSubscriptions = activeSubscriptions;
    }

    public long getExpiredSubscriptions() {
        return expiredSubscriptions;
    }

    public void setExpiredSubscriptions(long expiredSubscriptions) {
        this.expiredSubscriptions = expiredSubscriptions;
    }

    public long getRenewalsToday() {
        return renewalsToday;
    }

    public void setRenewalsToday(long renewalsToday) {
        this.renewalsToday = renewalsToday;
    }

    public long getRenewalsThisWeek() {
        return renewalsThisWeek;
    }

    public void setRenewalsThisWeek(long renewalsThisWeek) {
        this.renewalsThisWeek = renewalsThisWeek;
    }

    public List<RevenueValueDTO> getMonthlyRevenueChart() {
        return monthlyRevenueChart;
    }

    public void setMonthlyRevenueChart(List<RevenueValueDTO> monthlyRevenueChart) {
        this.monthlyRevenueChart = monthlyRevenueChart;
    }

    public List<RevenueValueDTO> getRevenueByPlan() {
        return revenueByPlan;
    }

    public void setRevenueByPlan(List<RevenueValueDTO> revenueByPlan) {
        this.revenueByPlan = revenueByPlan;
    }

    public List<PlanCountDTO> getPlanDistribution() {
        return planDistribution;
    }

    public void setPlanDistribution(List<PlanCountDTO> planDistribution) {
        this.planDistribution = planDistribution;
    }

    public List<RenewalsTrendDTO> getRenewalsTrend() {
        return renewalsTrend;
    }

    public void setRenewalsTrend(List<RenewalsTrendDTO> renewalsTrend) {
        this.renewalsTrend = renewalsTrend;
    }
}
