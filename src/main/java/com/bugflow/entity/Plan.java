package com.bugflow.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "plans")
public class Plan {

    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String displayName;
    private String description;
    private BigDecimal price;
    private String currency = "INR";
    private String billingPeriod;
    private Integer durationDays;
    private Integer projectLimit;
    private Integer memberLimit;
    private Integer issueLimit;
    private boolean highlight;
    private String accentColor;
    private Integer displayOrder;
    private List<String> features = new ArrayList<>();
    private boolean isActive = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public Integer getProjectLimit() { return projectLimit; }
    public void setProjectLimit(Integer projectLimit) { this.projectLimit = projectLimit; }
    public Integer getMemberLimit() { return memberLimit; }
    public void setMemberLimit(Integer memberLimit) { this.memberLimit = memberLimit; }
    public Integer getIssueLimit() { return issueLimit; }
    public void setIssueLimit(Integer issueLimit) { this.issueLimit = issueLimit; }
    public boolean isHighlight() { return highlight; }
    public void setHighlight(boolean highlight) { this.highlight = highlight; }
    public String getAccentColor() { return accentColor; }
    public void setAccentColor(String accentColor) { this.accentColor = accentColor; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features == null ? new ArrayList<>() : features; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
