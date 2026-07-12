package com.bugflow.service.admin;

import java.util.List;

import com.bugflow.dto.admin.PlanDTO;

public interface AdminPlanService {
    List<PlanDTO> getPlans();
    PlanDTO getPlan(String id);
    PlanDTO createPlan(PlanDTO request);
    PlanDTO updatePlan(String id, PlanDTO request);
    void deletePlan(String id);
}
