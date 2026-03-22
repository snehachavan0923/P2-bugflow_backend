package com.bugflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bugflow.entity.Issue;
import com.bugflow.repository.IssueRepository;



@Service

public class IssueService {

    private IssueRepository issueRepository;

    public Issue createIssue(Issue issue) {
        return issueRepository.save(issue);
    }

    public List<Issue> getIssuesByProject(String projectId) {
        return issueRepository.findByProjectId(projectId);
    }

    public Issue updateIssueStatus(String id, String status) {
        Issue issue = issueRepository.findById(id).orElseThrow();
        issue.setStatus(status);
        return issueRepository.save(issue);
    }
}