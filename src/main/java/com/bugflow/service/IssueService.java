package com.bugflow.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bugflow.dto.IssueDTO;
import com.bugflow.entity.Issue;
import com.bugflow.entity.Project;
import com.bugflow.entity.ProjectMember;
import com.bugflow.entity.User;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.IssueRepository;
import com.bugflow.repository.ProjectMemberRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.UserRepository;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;
        @Autowired
        private com.bugflow.service.SubscriptionValidator subscriptionValidator;

    public Issue createIssue(
            String projectId,
            IssueDTO dto,
            String imageUrl) {

    	ProjectMember developer =
    	        projectMemberRepository
    	                .findByProjectIdAndUserId(
    	                        projectId,
    	                        dto.getDeveloperUserId())
    	                .orElseThrow(() ->
    	                        new ResourceNotFoundException(
    	                                "Developer not found"));
    	if(!developer.getRole().name()
    	        .equals("Developer")) {

    	    throw new BadRequestException(
    	            "Selected member is not a developer");
    	}
    	

    	ProjectMember tester =
    	        projectMemberRepository
    	                .findByProjectIdAndUserId(
    	                        projectId,
    	                        dto.getTesterUserId())
    	                .orElseThrow(() ->
    	                        new ResourceNotFoundException(
    	                                "Tester not found"));
    	if(!tester.getRole().name()
    	        .equals("Tester")) {

    	    throw new BadRequestException(
    	            "Selected member is not a tester");
    	}

        Issue issue = new Issue();

        Project project =
                projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        issue.setProjectId(projectId); 
        issue.setProjectName(project.getName());

        issue.setTitle(dto.getTitle());
        issue.setDescription(dto.getDescription());

        issue.setPriority(dto.getPriority());
        
        issue.setAssignedToUserId(
                developer.getUserId());

        issue.setAssignedToName(
                developer.getName());

        issue.setAssignedToEmail(
                developer.getEmail());

        issue.setAssignedToRole(
                "Developer");

        issue.setDeveloperId(
                developer.getUserId());

        issue.setDeveloperName(
                developer.getName());

        issue.setDeveloperEmail(
                developer.getEmail());

        issue.setTesterId(
                tester.getUserId());

        issue.setTesterName(
                tester.getName());

        issue.setTesterEmail(
                tester.getEmail());

        issue.setImageUrl(imageUrl);

        issue.setStatus("Open");

                // Validate issue limit for the organization's subscription
                subscriptionValidator.checkIssueLimit(project.getOrganizationId());

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

        

        issue.setResolutionImageUrl(
                resolutionImageUrl);

        issue.setStatus("Review");

        issue.setAssignedToUserId(
                issue.getTesterId());

        issue.setAssignedToName(
                issue.getTesterName());

        issue.setAssignedToEmail(
                issue.getTesterEmail());

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

        issue.setTitle(dto.getTitle());

        issue.setDescription(dto.getDescription());

        issue.setPriority(dto.getPriority());

        // Update Developer
        if(dto.getDeveloperUserId() != null &&
           !dto.getDeveloperUserId().isBlank()) {

            ProjectMember developer =
                    projectMemberRepository
                            .findByProjectIdAndUserId(
                                    issue.getProjectId(),
                                    dto.getDeveloperUserId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Developer not found"));
            if(!developer.getRole().name()
                    .equals("Developer")) {

                throw new BadRequestException(
                        "Selected member is not a developer");
            }

            issue.setDeveloperId(
                    developer.getUserId());

            issue.setDeveloperName(
                    developer.getName());

            issue.setDeveloperEmail(
                    developer.getEmail());

            // If currently assigned to developer
            // update current assignee also
            if("Developer".equals(
                    issue.getAssignedToRole())) {

                issue.setAssignedToUserId(
                        developer.getUserId());

                issue.setAssignedToName(
                        developer.getName());

                issue.setAssignedToEmail(
                        developer.getEmail());

                issue.setAssignedToRole(
                        "Developer");
            }
        }

        // Update Tester
        if(dto.getTesterUserId() != null &&
           !dto.getTesterUserId().isBlank()) {

            ProjectMember tester =
                    projectMemberRepository
                            .findByProjectIdAndUserId(
                                    issue.getProjectId(),
                                    dto.getTesterUserId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Tester not found"));
            if(!tester.getRole().name()
                    .equals("Tester")) {

                throw new BadRequestException(
                        "Selected member is not a tester");
            }

            issue.setTesterId(
                    tester.getUserId());

            issue.setTesterName(
                    tester.getName());

            issue.setTesterEmail(
                    tester.getEmail());

            // If currently assigned to tester
            // update current assignee also
            if("Tester".equals(
                    issue.getAssignedToRole())) {

                issue.setAssignedToUserId(
                        tester.getUserId());

                issue.setAssignedToName(
                        tester.getName());

                issue.setAssignedToEmail(
                        tester.getEmail());

                issue.setAssignedToRole(
                        "Tester");
            }
        }

        return issueRepository.save(issue);
    }

    public void deleteIssue(
            String issueId,
            String ownerEmail) {

        User owner =
                userRepository.findByEmail(ownerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        Issue issue =
                issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Issue not found"));

        Project project =
                projectRepository.findById(
                        issue.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        if (!project.getOwnerId().equals(owner.getId())) {

            throw new BadRequestException(
                    "You are not allowed to delete this issue.");
        }

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
    
    public List<Issue> getDeveloperIssuesByProject(
            String projectId,
            String email) {

        return issueRepository.findByProjectId(
                projectId);
    }
    
    public List<Issue> getTesterIssuesByProject(
            String projectId) {

        return issueRepository
                .findByProjectId(projectId);
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

        issue.setAssignedToUserId(
                issue.getTesterId());

        issue.setAssignedToName(
                issue.getTesterName());

        issue.setAssignedToEmail(
                issue.getTesterEmail());

        issue.setAssignedToRole(
                "Tester");

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
    
    public List<Issue> getOrganizationIssues(
            String email) {

        User owner =
                userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        if(owner.getOrganizationId() == null) {

            throw new ResourceNotFoundException(
                    "Organization not found");
        }

        List<Project> projects =
                projectRepository.findByOrganizationId(
                        owner.getOrganizationId());

        List<String> projectIds =
                projects.stream()
                        .map(Project::getId)
                        .toList();

        if(projectIds.isEmpty()) {
            return List.of();
        }

        return issueRepository.findByProjectIdIn(
                projectIds);
    }
    
    
} 