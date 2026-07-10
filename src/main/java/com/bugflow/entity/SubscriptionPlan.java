package com.bugflow.entity;

public enum SubscriptionPlan {
    FREE(1, 5, 100),
    STARTER(5, 25, null),
    BUSINESS(15, 100, null),
    ENTERPRISE(null, null, null);

    private final Integer projectLimit;
    private final Integer memberLimit;
    private final Integer issueLimit;

    SubscriptionPlan(Integer projectLimit, Integer memberLimit, Integer issueLimit) {
        this.projectLimit = projectLimit;
        this.memberLimit = memberLimit;
        this.issueLimit = issueLimit;
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

    public String getDisplayName() {
        return switch (this) {
            case FREE -> "Free";
            case STARTER -> "Starter";
            case BUSINESS -> "Business";
            case ENTERPRISE -> "Enterprise";
        };
    }
}
