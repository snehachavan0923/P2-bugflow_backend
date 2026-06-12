package com.bugflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bugflow.dto.AddMemberRequest;
import com.bugflow.entity.ProjectMember;
import com.bugflow.entity.Role;
import com.bugflow.service.ProjectMemberService;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "*")
public class ProjectMemberController {


private final ProjectMemberService projectMemberService;


public ProjectMemberController(
        ProjectMemberService projectMemberService) {

    this.projectMemberService = projectMemberService;
}

@PostMapping("/{projectId}/team")
public ResponseEntity<ProjectMember> addMember(
        @PathVariable String projectId,
        @RequestBody AddMemberRequest request) {

    return ResponseEntity.ok(
            projectMemberService.addMember(
                    projectId,
                    request));
}

@GetMapping("/{projectId}/team")
public ResponseEntity<List<ProjectMember>> getMembers(
        @PathVariable String projectId) {

    return ResponseEntity.ok(
            projectMemberService.getMembers(
                    projectId));
}

@PutMapping("/{projectId}/team/{memberId}")
public ResponseEntity<ProjectMember> updateRole(
        @PathVariable String projectId,
        @PathVariable String memberId,
        @RequestParam Role role) {

    return ResponseEntity.ok(
            projectMemberService.updateRole(
                    memberId,
                    role));
}

@DeleteMapping("/{projectId}/team/{memberId}")
public ResponseEntity<String> removeMember(
        @PathVariable String projectId,
        @PathVariable String memberId) {

    projectMemberService.removeMember(
            projectId,
            memberId);

    return ResponseEntity.ok(
            "Member removed successfully");
}


}
