package com.bugflow.service.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bugflow.dto.admin.PlanDTO;
import com.bugflow.entity.Plan;
import com.bugflow.exception.BadRequestException;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.PlanRepository;

@Service
public class AdminPlanServiceImpl implements AdminPlanService {

    private final PlanRepository planRepository;

    public AdminPlanServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public List<PlanDTO> getPlans() {
        return planRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public PlanDTO getPlan(String id) {
        return toDto(findPlan(id));
    }

    @Override
    public PlanDTO createPlan(PlanDTO request) {

        validate(request, null);

        Plan plan = new Plan();

        apply(request, plan);

        LocalDateTime now = LocalDateTime.now();

        plan.setCreatedAt(now);
        plan.setUpdatedAt(now);

        return toDto(planRepository.save(plan));
    }

    @Override
    public PlanDTO updatePlan(String id, PlanDTO request) {

        Plan plan = findPlan(id);

        validate(request, plan.getId());

        apply(request, plan);

        plan.setUpdatedAt(LocalDateTime.now());

        return toDto(planRepository.save(plan));
    }

    @Override
    public void deletePlan(String id) {

        Plan plan = findPlan(id);

        plan.setActive(false);
        plan.setUpdatedAt(LocalDateTime.now());

        planRepository.save(plan);
    }

    private Plan findPlan(String id) {

        return planRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Plan not found"));
    }

    private void validate(PlanDTO request, String currentId) {

        if (request == null
                || request.getName() == null
                || request.getName().isBlank()) {

            throw new BadRequestException("Plan name is required");
        }

        String name = request.getName()
                .trim()
                .toUpperCase();

        if (request.getDisplayName() == null || request.getDisplayName().isBlank()) {
            throw new BadRequestException("Display name is required");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new BadRequestException("Description is required");
        }
        if (request.getCurrency() == null || request.getCurrency().isBlank()) {
            throw new BadRequestException("Currency is required");
        }
        if (!"ENTERPRISE".equals(name)
                && (request.getBillingPeriod() == null || request.getBillingPeriod().isBlank())) {
            throw new BadRequestException("Billing period is required for non-enterprise plans");
        }
        if (request.getDurationDays() != null && request.getDurationDays() <= 0) {
            throw new BadRequestException("Duration must be greater than zero");
        }
        boolean unlimited = request.getProjectLimit() == null
                && request.getMemberLimit() == null
                && request.getIssueLimit() == null;
        if (!"ENTERPRISE".equals(name) && !unlimited && request.getDurationDays() == null) {
            throw new BadRequestException("Duration is required for limited plans");
        }

        planRepository.findByNameIgnoreCase(name)
                .filter(plan -> !plan.getId().equals(currentId))
                .ifPresent(plan -> {
                    throw new BadRequestException("Plan name already exists");
                });

        if (request.getPrice() != null
                && request.getPrice().compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException("Price cannot be negative");
        }

        validateLimit(request.getProjectLimit(), "Project limit");
        validateLimit(request.getMemberLimit(), "Member limit");
        validateLimit(request.getIssueLimit(), "Issue limit");

        if ("FREE".equals(name)
                && (request.getPrice() == null
                || request.getPrice().compareTo(BigDecimal.ZERO) != 0)) {

            throw new BadRequestException("FREE plan price must be zero");
        }

        if (!"ENTERPRISE".equals(name)
                && request.getPrice() == null) {

            throw new BadRequestException(
                    "Price is required for non-enterprise plans");
        }
    }

    private void validateLimit(Integer value, String label) {
        if (value != null && value < 0) {
            throw new BadRequestException(label + " cannot be negative");
        }
    }

    private void apply(PlanDTO source, Plan target) {

        target.setName(source.getName().trim().toUpperCase());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());

        target.setPrice(source.getPrice());

        target.setCurrency(
                source.getCurrency() == null
                        || source.getCurrency().isBlank()
                                ? "INR"
                                : source.getCurrency()
                                        .trim()
                                        .toUpperCase());

        target.setBillingPeriod(source.getBillingPeriod());
        target.setDurationDays(source.getDurationDays());

        target.setProjectLimit(source.getProjectLimit());
        target.setMemberLimit(source.getMemberLimit());
        target.setIssueLimit(source.getIssueLimit());

        target.setHighlight(source.isHighlight());
        target.setAccentColor(source.getAccentColor());

        target.setFeatures(source.getFeatures());

        target.setActive(source.isActive());
    }

    private PlanDTO toDto(Plan plan) {

        PlanDTO dto = new PlanDTO();

        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setDisplayName(plan.getDisplayName());
        dto.setDescription(plan.getDescription());

        dto.setPrice(plan.getPrice());
        dto.setCurrency(plan.getCurrency());
        dto.setBillingPeriod(plan.getBillingPeriod());

        dto.setDurationDays(plan.getDurationDays());

        dto.setProjectLimit(plan.getProjectLimit());
        dto.setMemberLimit(plan.getMemberLimit());
        dto.setIssueLimit(plan.getIssueLimit());

        dto.setHighlight(plan.isHighlight());
        dto.setAccentColor(plan.getAccentColor());

        dto.setFeatures(plan.getFeatures());

        dto.setActive(plan.isActive());

        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());

        return dto;
    }
}
