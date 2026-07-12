package com.bugflow.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bugflow.entity.Plan;
import com.bugflow.repository.PlanRepository;

@Component
public class PlanDataInitializer implements CommandLineRunner {

    private final PlanRepository planRepository;

    public PlanDataInitializer(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) {

        if (planRepository.count() > 0) {
            return;
        }

        planRepository.saveAll(List.of(

                createFreePlan(),
                createStarterPlan(),
                createBusinessPlan(),
                createEnterprisePlan()

        ));

        System.out.println("Default subscription plans inserted.");
    }

    private Plan createFreePlan() {

        Plan plan = new Plan();

        plan.setName("FREE");
        plan.setDisplayName("Free");
        plan.setDescription("For solo founders and early experiments.");
        plan.setPrice(BigDecimal.ZERO);
        plan.setCurrency("INR");
        plan.setBillingPeriod("FOREVER");
        plan.setDurationDays(null);

        plan.setProjectLimit(1);
        plan.setMemberLimit(5);
        plan.setIssueLimit(100);

        plan.setHighlight(false);
        plan.setAccentColor("#64748B");

        plan.setFeatures(List.of(
                "1 Project",
                "5 Members",
                "100 Issues"));

        plan.setActive(true);
        plan.setDisplayOrder(1);

        return plan;
    }

    private Plan createStarterPlan() {

        Plan plan = new Plan();

        plan.setName("STARTER");
        plan.setDisplayName("Starter");
        plan.setDescription("For growing product teams.");

        plan.setPrice(BigDecimal.valueOf(399));
        plan.setCurrency("INR");
        plan.setBillingPeriod("MONTHLY");
        plan.setDurationDays(30);

        plan.setProjectLimit(5);
        plan.setMemberLimit(25);
        plan.setIssueLimit(null);

        plan.setHighlight(true);
        plan.setAccentColor("#4F46E5");

        plan.setFeatures(List.of(
                "5 Projects",
                "25 Members",
                "Unlimited Issues"));

        plan.setActive(true);
        plan.setDisplayOrder(2);

        return plan;
    }

    private Plan createBusinessPlan() {

        Plan plan = new Plan();

        plan.setName("BUSINESS");
        plan.setDisplayName("Business");
        plan.setDescription("For scaling organizations.");

        plan.setPrice(BigDecimal.valueOf(999));
        plan.setCurrency("INR");
        plan.setBillingPeriod("MONTHLY");
        plan.setDurationDays(30);

        plan.setProjectLimit(15);
        plan.setMemberLimit(100);
        plan.setIssueLimit(null);

        plan.setHighlight(false);
        plan.setAccentColor("#7C3AED");

        plan.setFeatures(List.of(
                "15 Projects",
                "100 Members",
                "Unlimited Issues"));

        plan.setActive(true);
        plan.setDisplayOrder(3);
        return plan;
    }

    private Plan createEnterprisePlan() {

        Plan plan = new Plan();

        plan.setName("ENTERPRISE");
        plan.setDisplayName("Enterprise");
        plan.setDescription("For large organizations.");

        plan.setPrice(null);
        plan.setCurrency("INR");
        plan.setBillingPeriod(null);
        plan.setDurationDays(null);

        plan.setProjectLimit(null);
        plan.setMemberLimit(null);
        plan.setIssueLimit(null);

        plan.setHighlight(false);
        plan.setAccentColor("#0EA5E9");

        plan.setFeatures(List.of(
                "Unlimited Projects",
                "Unlimited Members",
                "Unlimited Issues"));

        plan.setActive(true);
        plan.setDisplayOrder(4);

        return plan;
    }
}