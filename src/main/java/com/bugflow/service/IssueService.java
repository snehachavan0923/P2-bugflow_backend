package com.bugflow.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bugflow.dto.IssueDTO;
import com.bugflow.entity.Issue;
import com.bugflow.entity.ProjectMember;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.IssueRepository;
import com.bugflow.repository.ProjectMemberRepository;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public Issue createIssue(
            String projectId,
            IssueDTO dto,
            String imageUrl) {

        ProjectMember member =
                projectMemberRepository.findById(
                        dto.getAssignedToUserId())
                .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Assigned member not found"));

        Issue issue = new Issue();

        issue.setProjectId(projectId);
        issue.setProjectName(dto.getProjectName());

        issue.setTitle(dto.getTitle());
        issue.setDescription(dto.getDescription());

        issue.setPriority(dto.getPriority());
        
        issue.setAssignedToName(
                member.getName());

        issue.setAssignedToUserId(
                member.getUserId());

        issue.setAssignedToEmail(
                member.getEmail());

        issue.setAssignedToRole(
                member.getRole().name());

        issue.setDeveloperId(
                member.getUserId());

        issue.setDeveloperName(
                member.getName());

        issue.setDeveloperEmail(
                member.getEmail());

        issue.setImageUrl(imageUrl);

        issue.setStatus("Open");

        return issueRepository.save(issue);
    }

    public List<Issue> getIssuesByProject(
            String projectId) {

        return issueRepository.findByProjectId(
                projectId);
    }

    {/*  public List<Issue> getMyIssues(
            String email) {

        return issueRepository.findByAssignedToEmail(
                email);
    } */} 

    {/* public Issue updateIssue(
            String id,
            Issue updated) {

        Issue issue =
                issueRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found"));

        issue.setTitle(updated.getTitle());

        issue.setDescription(
                updated.getDescription());

        issue.setPriority(
                updated.getPriority());

        issue.setStatus(
                updated.getStatus());

        return issueRepository.save(issue);
    }
     */}
    
    public Issue updateStatus(
            String issueId,
            String status) {

        Issue issue =
                issueRepository.findById(issueId)
                .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Issue not found"));

        issue.setStatus(status);

        return issueRepository.save(issue);
    }

    public Issue resolveIssue(
            String issueId,
            String resolutionImageUrl) {

        Issue issue =
                issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found"));

        ProjectMember tester =
                projectMemberRepository
                        .findByProjectId(
                                issue.getProjectId())
                        .stream()
                        .filter(m ->
                                m.getRole().name()
                                 .equals("Tester"))
                        .findFirst()
                        .orElseThrow(() ->
                              new BadRequestException(
                        	    "No tester assigned to project"));

        issue.setResolutionImageUrl(
                resolutionImageUrl);

        issue.setStatus("Review");

        issue.setAssignedToUserId(
                tester.getUserId());

        issue.setAssignedToName(
                tester.getName());

        issue.setAssignedToEmail(
                tester.getEmail());

        issue.setAssignedToRole(
                "Tester");

        return issueRepository.save(issue);
    }

    public Issue updateIssue(
            String id,
            IssueDTO dto) {

        Issue issue =
                issueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found"));

        issue.setTitle(
                dto.getTitle());

        issue.setDescription(
                dto.getDescription());

        issue.setPriority(
                dto.getPriority());

        if(dto.getAssignedToUserId() != null &&
           !dto.getAssignedToUserId().isBlank()) {

        	ProjectMember member =
        	        projectMemberRepository
        	            .findByProjectIdAndUserId(
        	                    issue.getProjectId(),
        	                    dto.getAssignedToUserId())
        	            .orElseThrow(() ->
        	                    new ResourceNotFoundException(
        	                            "Assigned member not found"));
            
 
            issue.setAssignedToUserId(
                    member.getUserId());
            issue.setAssignedToName(
                    member.getName());

            issue.setAssignedToEmail(
                    member.getEmail());

            String role = member.getRole().name();

            issue.setAssignedToRole(role);

            if(role.equals("Developer")) {

                issue.setDeveloperId(
                        member.getUserId());

                issue.setDeveloperName(
                        member.getName());

                issue.setDeveloperEmail(
                        member.getEmail());
            }
            
            switch(role) {

                case "Tester":
                    issue.setStatus("Review");
                    break;

                case "Developer":
                    issue.setStatus("Open");
                    break;

                default:
                    break;
            }
            
        }

        return issueRepository.save(issue);
    }

    public void deleteIssue(String id) {

        Issue issue =
                issueRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "Issue not found"));

        issueRepository.delete(issue);
    }
    
    
    public List<Issue> getAllIssues() {

        return issueRepository.findAll();
    }

    {/* public List<Issue> getReviewIssues(
            String email) {

        return issueRepository
                .findByAssignedToEmailAndStatus(
                        email,
                        "Review");
    }  */}
    
    public List<Issue> getMyIssuesByProject(
            String projectId,
            String email) {

        return issueRepository
                .findByProjectIdAndAssignedToEmail(
                        projectId,
                        email);
    }
    
    public List<Issue> getReviewIssuesByProject(
            String projectId,
            String email) {

        return issueRepository
                .findByProjectIdAndAssignedToEmailAndStatus(
                        projectId,
                        email,
                        "Review");
    }
    
    public List<Issue> getDeveloperTasks(
            String email) {

        return issueRepository.findByAssignedToEmail(
                email);
    }
    
    public Issue approveIssue(
            String issueId) {

        Issue issue =
                issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found"));

        issue.setStatus("Done");

        return issueRepository.save(issue);
    }
    
    public Issue rejectIssue(
            String issueId) {

        Issue issue =
                issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found"));

        issue.setStatus(
                "In Progress");

        issue.setAssignedToUserId(
                issue.getDeveloperId());

        issue.setAssignedToName(
                issue.getDeveloperName());

        issue.setAssignedToEmail(
                issue.getDeveloperEmail());

        issue.setAssignedToRole(
                "Developer");

        return issueRepository.save(issue);
    }
    
    
}