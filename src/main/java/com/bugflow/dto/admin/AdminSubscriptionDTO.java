package com.bugflow.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminSubscriptionDTO {

    public String id;

    public String organizationId;

    public String organizationName;

    public String ownerId;

    public String ownerName;

    public String ownerEmail;

    public String plan;

    public String status;

    public BigDecimal amount;

    public String currency;

    public String paymentStatus;

    public String transactionId;

    public String paymentMethod;

    public LocalDateTime startDate;

    public LocalDateTime expiryDate;

    public LocalDateTime lastPaymentDate;

    public Integer projectLimit;

    public Integer memberLimit;

    public Integer issueLimit;

    public Integer currentProjects;

    public Integer currentMembers;

    public Integer currentIssues;

    public Long daysRemaining;

    public List<AdminPaymentDTO> paymentHistory;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDateTime lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public Integer getProjectLimit() {
        return projectLimit;
    }

    public void setProjectLimit(Integer projectLimit) {
        this.projectLimit = projectLimit;
    }

    public Integer getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(Integer memberLimit) {
        this.memberLimit = memberLimit;
    }

    public Integer getIssueLimit() {
        return issueLimit;
    }

    public void setIssueLimit(Integer issueLimit) {
        this.issueLimit = issueLimit;
    }

    public Integer getCurrentProjects() {
        return currentProjects;
    }

    public void setCurrentProjects(Integer currentProjects) {
        this.currentProjects = currentProjects;
    }

    public Integer getCurrentMembers() {
        return currentMembers;
    }

    public void setCurrentMembers(Integer currentMembers) {
        this.currentMembers = currentMembers;
    }

    public Integer getCurrentIssues() {
        return currentIssues;
    }

    public void setCurrentIssues(Integer currentIssues) {
        this.currentIssues = currentIssues;
    }

    public Long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public List<AdminPaymentDTO> getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(List<AdminPaymentDTO> paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    
}
