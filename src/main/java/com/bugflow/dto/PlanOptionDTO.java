package com.bugflow.dto;

public class PlanOptionDTO {

    private String name;
    private String displayName;
    private Integer projectLimit;
    private Integer memberLimit;
    private Integer issueLimit;
    private boolean unlimited;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public boolean isUnlimited() {
        return unlimited;
    }

    public void setUnlimited(boolean unlimited) {
        this.unlimited = unlimited;
    }
}
