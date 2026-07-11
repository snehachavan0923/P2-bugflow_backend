package com.bugflow.entity;

import java.math.BigDecimal;

public enum SubscriptionPlan {
    FREE("Free", BigDecimal.ZERO, null, 1, 5, 100),
    STARTER("Starter", new BigDecimal("399"), 30, 5, 25, null),
    BUSINESS("Business", new BigDecimal("999"), 30, 15, 100, null),
    ENTERPRISE("Enterprise", null, null, null, null, null);

    private final String displayName;
    private final BigDecimal price;
    private final Integer durationDays;
    private final Integer projectLimit;
    private final Integer memberLimit;
    private final Integer issueLimit;

    SubscriptionPlan(String displayName, BigDecimal price, Integer durationDays,
            Integer projectLimit, Integer memberLimit, Integer issueLimit) {
        this.displayName = displayName;
        this.price = price;
        this.durationDays = durationDays;
        this.projectLimit = projectLimit;
        this.memberLimit = memberLimit;
        this.issueLimit = issueLimit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public Integer getProjectLimit() {
        return projectLimit;
    }

    public Integer getMemberLimit() {
        return memberLimit;
    }

    public Integer getIssueLimit() {
        return issueLimit;
    }

    public boolean isUnlimited(Integer value) {
        return value == null;
    }

}
