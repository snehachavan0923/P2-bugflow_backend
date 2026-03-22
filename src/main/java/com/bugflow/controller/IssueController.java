package com.bugflow.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bugflow.entity.Issue;
import com.bugflow.service.IssueService;

@RestController
@RequestMapping("/api/issues")

@CrossOrigin("*")
public class IssueController {

    private  IssueService issueService;

    @PostMapping
    public Issue createIssue(@RequestBody Issue issue) {
        return issueService.createIssue(issue);
    }

    @GetMapping("/project/{projectId}")
    public List<Issue> getIssues(@PathVariable String projectId) {
        return issueService.getIssuesByProject(projectId);
    }

    @PutMapping("/{id}/status")
    public Issue updateStatus(@PathVariable String id, @RequestParam String status) {
        return issueService.updateIssueStatus(id, status);
    }
}