package com.bugflow.controller;
import org.springframework.security.core.Authentication;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bugflow.dto.IssueDTO;
import com.bugflow.entity.Issue;
import com.bugflow.service.IssueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bugflow.service.FileStorageService; 
@RestController
@RequestMapping("/projects/{projectId}/issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private FileStorageService fileStorageService;

  
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Issue> createIssue(
            @PathVariable String projectId,
            @RequestPart("data") String data,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        IssueDTO dto = mapper.readValue(data, IssueDTO.class);

        String imageUrl = null;

        if (file != null && !file.isEmpty()) {
            imageUrl = fileStorageService.upload(file);
        }

        return ResponseEntity.ok(
                issueService.createIssue(projectId, dto, imageUrl)
        );
    }


    @GetMapping
    public ResponseEntity<List<Issue>> getIssues(@PathVariable String projectId) {
        return ResponseEntity.ok(issueService.getIssuesByProject(projectId));
    }
    
    {/* @GetMapping("/my-tasks")
    public ResponseEntity<List<Issue>> getMyTasks(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                issueService.getMyIssues(email));
    } */}
    

    @PutMapping("/{issueId}/status")
    public ResponseEntity<Issue> updateStatus(
            @PathVariable String issueId,
            @RequestParam String status) {

        return ResponseEntity.ok(issueService.updateStatus(issueId, status));
    }
    @PutMapping("/{issueId}")
    public ResponseEntity<Issue> updateIssue(
            @PathVariable String projectId,   
            @PathVariable String issueId,
            @RequestBody IssueDTO dto) {

        return ResponseEntity.ok(issueService.updateIssue(issueId, dto));
    }
    
    @PutMapping(value = "/{issueId}/resolve", consumes = "multipart/form-data")
    public ResponseEntity<Issue> resolveIssue(
            @PathVariable String projectId,   
            @PathVariable String issueId,
            @RequestPart("file") MultipartFile file) {

        String imageUrl = fileStorageService.upload(file);

        return ResponseEntity.ok(
                issueService.resolveIssue(issueId, imageUrl)
        );
    }

 
    @DeleteMapping("/{issueId}")
    public ResponseEntity<String> deleteIssue(@PathVariable String issueId) {
        issueService.deleteIssue(issueId);
        return ResponseEntity.ok("Deleted");
    }
    
  @GetMapping("/all")
    public ResponseEntity<List<Issue>>
    getAllIssues() {

        return ResponseEntity.ok(
                issueService.getAllIssues());
    }
    
  {/* @GetMapping("/review")
    public ResponseEntity<List<Issue>>
    getReviewIssues(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                issueService.getReviewIssues(
                        email));
    } */}
    
    @GetMapping("/project-my-tasks")
    public ResponseEntity<List<Issue>>
    getProjectTasks(
            @PathVariable String projectId,
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                issueService.getMyIssuesByProject(
                        projectId,
                        email));
    }
    
    @GetMapping("/project-review")
    public ResponseEntity<List<Issue>>
    getProjectReviewIssues(
            @PathVariable String projectId,
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                issueService.getReviewIssuesByProject(
                        projectId,
                        email));
    }
    
    
    @PutMapping("/{issueId}/approve")
    public ResponseEntity<Issue>
    approveIssue(
            @PathVariable String issueId) { 

        return ResponseEntity.ok(
                issueService.approveIssue(
                        issueId));
    }
    
    @PutMapping("/{issueId}/reject")
    public ResponseEntity<Issue>
    rejectIssue(
            @PathVariable String issueId) {

        return ResponseEntity.ok(
                issueService.rejectIssue(
                        issueId));
    }
   
}
