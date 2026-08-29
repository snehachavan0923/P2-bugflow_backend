package com.bugflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bugflow.dto.AddMemberRequest;
import com.bugflow.dto.ChangePasswordRequest;
import com.bugflow.dto.CreateInvitationRequest;
import com.bugflow.entity.ProjectMember;
import com.bugflow.entity.Role;
import com.bugflow.service.InvitationService;
import com.bugflow.service.ProjectMemberService;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "*")
public class ProjectMemberController {


private final ProjectMemberService projectMemberService;
private final InvitationService invitationService;


	public ProjectMemberController(
		ProjectMemberService projectMemberService,
		InvitationService invitationService) {

	this.projectMemberService = projectMemberService;
	this.invitationService = invitationService;
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

	@PostMapping("/{projectId}/invitations")
	public ResponseEntity<?> createInvitation(
	        @PathVariable String projectId,
	        @RequestBody CreateInvitationRequest request,
	        java.security.Principal principal) {

	    // principal name is the authenticated user's email (set by JwtFilter)
	    String inviterEmail = principal.getName();

	    var invitation = invitationService.createInvitation(projectId, request, inviterEmail);

	    return ResponseEntity.ok(invitation);
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
	@PutMapping("/{projectId}/team/{memberId}/password")
	public ResponseEntity<String> changePassword(
	        @PathVariable String projectId,
	        @PathVariable String memberId,
	        @RequestBody ChangePasswordRequest request) {

	    projectMemberService.changeMemberPassword(
	            projectId,
	            memberId,
	            request.getPassword());

	    return ResponseEntity.ok(
	            "Password updated successfully");
	}

}
