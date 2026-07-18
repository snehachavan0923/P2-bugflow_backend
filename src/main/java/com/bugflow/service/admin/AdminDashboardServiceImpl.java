package com.bugflow.service.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bugflow.dto.ActivityDTO;
import com.bugflow.dto.admin.AdminDashboardStatsDTO;
import com.bugflow.entity.Organization;
import com.bugflow.entity.Payment;
import com.bugflow.entity.PaymentStatus;
import com.bugflow.entity.Project;
import com.bugflow.entity.Subscription;
import com.bugflow.entity.SubscriptionStatus;
import com.bugflow.entity.User;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.PaymentRepository;
import com.bugflow.repository.ProjectRepository;
import com.bugflow.repository.SubscriptionRepository;
import com.bugflow.repository.UserRepository;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;

    public AdminDashboardServiceImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository,
            SubscriptionRepository subscriptionRepository,
            PaymentRepository paymentRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public AdminDashboardStatsDTO getDashboardStats() {
        // Fetch basic statistics
        long totalOrganizations = organizationRepository.count();
        long totalUsers = userRepository.count();
        long totalProjects = projectRepository.count();
        
        // Fetch subscription statistics
        long activeSubscriptions = subscriptionRepository
                .findAll()
                .stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
                .count();
        
        long expiredSubscriptions = subscriptionRepository
                .findAll()
                .stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.EXPIRED)
                .count();

        // Calculate total revenue from successful payments
        BigDecimal totalRevenue = paymentRepository
                .findAll()
                .stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Fetch recent activities
        List<ActivityDTO> recentActivities = generateRecentActivities();

        return new AdminDashboardStatsDTO(
                totalOrganizations,
                totalUsers,
                totalProjects,
                totalRevenue,
                activeSubscriptions,
                expiredSubscriptions,
                recentActivities
        );
    }

    /**
     * Generate recent activities from existing entities.
     * Converts Organization, Subscription, Payment, Project, and User records
     * into ActivityDTOs, merges them, and returns the 10 most recent.
     */
    private List<ActivityDTO> generateRecentActivities() {
        List<ActivityDTO> activities = new ArrayList<>();

        // Get all existing records with timestamps
        List<Organization> organizations = organizationRepository.findAll();
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();
        List<Project> projects = projectRepository.findAll();
        List<User> users = userRepository.findAll();

        // Convert Organizations to activities
        for (Organization org : organizations) {
            User owner = userRepository.findById(org.getOwnerId()).orElse(null);
            String ownerName = owner != null ? owner.getName() : "Unknown User";
            
            activities.add(new ActivityDTO(
                    "ORGANIZATION",
                    "Organization Created",
                    ownerName + " created organization " + org.getName(),
                    ownerName,
                    org.getCreatedAt(),
                    "building"
            ));
        }

        // Convert Subscriptions to activities (upgrades)
        for (Subscription subscription : subscriptions) {
            User owner = userRepository.findById(subscription.getOwnerId()).orElse(null);
            String ownerName = owner != null ? owner.getName() : "Unknown User";
            
            // Only add upgrade activities if plan is not FREE
            if (subscription.getPlan() != null && !subscription.getPlan().equals("FREE")) {
                activities.add(new ActivityDTO(
                        "SUBSCRIPTION",
                        "Plan Upgraded",
                        ownerName + " upgraded to " + subscription.getPlan() + " plan",
                        ownerName,
                        subscription.getCreatedAt(),
                        "trending-up"
                ));
            }
        }

        // Convert Payments to activities (purchases) - only successful payments
        for (Payment payment : payments) {
            if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
                User owner = userRepository.findById(payment.getOwnerId()).orElse(null);
                String ownerName = owner != null ? owner.getName() : "Unknown User";
                
                String amount = payment.getAmount() != null ? 
                    payment.getAmount().toString() : "0";
                
                activities.add(new ActivityDTO(
                        "PAYMENT",
                        "Payment Received",
                        ownerName + " purchased " + payment.getPlan() + 
                        " plan for ₹" + amount,
                        ownerName,
                        payment.getCreatedAt(),
                        "credit-card"
                ));
            }
        }

        // Convert Projects to activities
        for (Project project : projects) {
            User owner = userRepository.findById(project.getOwnerId()).orElse(null);
            String ownerName = owner != null ? owner.getName() : "Unknown User";
            
            activities.add(new ActivityDTO(
                    "PROJECT",
                    "Project Created",
                    ownerName + " created project " + project.getName(),
                    ownerName,
                    project.getCreatedAt(),
                    "folder-plus"
            ));
        }

        // Convert Users to activities (new signups)
        for (User user : users) {
            activities.add(new ActivityDTO(
                    "USER",
                    "New User",
                    user.getName() + " joined BugFlow",
                    user.getName(),
                    user.getCreatedAt(),
                    "user-plus"
            ));
        }

        // Sort by createdAt in descending order and take top 10
        return activities
                .stream()
                .sorted(Comparator.comparing(ActivityDTO::getCreatedAt).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}
