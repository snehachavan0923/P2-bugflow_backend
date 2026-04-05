package com.bugflow.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bugflow.dto.IssueDTO;
import com.bugflow.entity.Issue;
import com.bugflow.repository.IssueRepository;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    public Issue createIssue(String projectId, IssueDTO dto, String imageUrl) {

        Issue issue = new Issue();

        issue.setProjectId(projectId);
        issue.setProjectName(dto.getProjectName());
        issue.setTitle(dto.getTitle());
        issue.setDescription(dto.getDescription());
        issue.setPriority(dto.getPriority());
        issue.setAssignedTo(dto.getAssignedTo());
        issue.setImageUrl(imageUrl);

        issue.setStatus("Open");

        return issueRepository.save(issue);
    }

    public List<Issue> getIssuesByProject(String projectId) {
        return issueRepository.findByProjectId(projectId);
    }

    public Issue updateIssue(String id, Issue updated) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        issue.setTitle(updated.getTitle());
        issue.setDescription(updated.getDescription());
        issue.setPriority(updated.getPriority());
        issue.setStatus(updated.getStatus());
        issue.setAssignedTo(updated.getAssignedTo());

        return issueRepository.save(issue);
    }
    
    public Issue updateStatus(String issueId, String status) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        issue.setStatus(status);

        return issueRepository.save(issue);
    }

 
    public Issue resolveIssue(String issueId, String resolutionImageUrl) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        issue.setResolutionImageUrl(resolutionImageUrl);
        issue.setStatus("Done"); 

        return issueRepository.save(issue);
    }
    
    public Issue updateIssue(String id, IssueDTO dto) {

        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found"));

        issue.setTitle(dto.getTitle());
        issue.setDescription(dto.getDescription());
        issue.setPriority(dto.getPriority());
        issue.setAssignedTo(dto.getAssignedTo());

        return issueRepository.save(issue);
    }
    
    public void deleteIssue(String id) {
        issueRepository.deleteById(id);
    }
    
}