package com.bugflow.service.admin;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.bugflow.dto.admin.AdminPaymentDTO;
import com.bugflow.dto.admin.AdminSubscriptionDTO;
import com.bugflow.dto.admin.PageResponseDTO;
import com.bugflow.entity.Organization;
import com.bugflow.entity.Payment;
import com.bugflow.entity.PaymentStatus;
import com.bugflow.entity.Subscription;
import com.bugflow.entity.SubscriptionStatus;
import com.bugflow.entity.User;
import com.bugflow.exception.ResourceNotFoundException;
import com.bugflow.repository.OrganizationRepository;
import com.bugflow.repository.PaymentRepository;
import com.bugflow.repository.SubscriptionRepository;
import com.bugflow.repository.UserRepository;

@Service
public class AdminSubscriptionServiceImpl
        implements AdminSubscriptionService {

    private final SubscriptionRepository subscriptions;
    private final PaymentRepository payments;
    private final OrganizationRepository organizations;
    private final UserRepository users;

    public AdminSubscriptionServiceImpl(
            SubscriptionRepository subscriptions,
            PaymentRepository payments,
            OrganizationRepository organizations,
            UserRepository users) {

        this.subscriptions = subscriptions;
        this.payments = payments;
        this.organizations = organizations;
        this.users = users;
    }

    @Override
    public PageResponseDTO<AdminSubscriptionDTO> subscriptions(
            String search,
            String plan,
            String status,
            String paymentStatus,
            int page,
            int size) {

        List<AdminSubscriptionDTO> all =
                subscriptions.findAll()
                        .stream()
                        .map(this::map)
                        .filter(subscription ->
                                matches(
                                        subscription,
                                        search,
                                        plan,
                                        status,
                                        paymentStatus))
                        .sorted(
                                Comparator.comparing(
                                        subscription -> subscription.startDate,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder())))
                        .toList();

        return page(all, page, size);
    }

    @Override
    public AdminSubscriptionDTO detail(String id) {

        Subscription subscription =
                subscriptions.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription not found"));

        return map(subscription);
    }

    @Override
    public PageResponseDTO<AdminPaymentDTO> payments(
            String search,
            String plan,
            String status,
            String from,
            String to,
            int page,
            int size) {

        LocalDateTime fromDate = parse(from, false);
        LocalDateTime toDate = parse(to, true);
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new com.bugflow.exception.BadRequestException("From date cannot be after to date");
        }

        List<AdminPaymentDTO> all =
                payments.findAll()
                        .stream()
                        .map(this::payment)
                        .filter(payment ->

                                (search == null
                                        || search.isBlank()
                                        || contains(payment.organizationName, search)
                                        || contains(payment.ownerName, search)
                                        || contains(payment.transactionId, search))

                                &&

                                (plan == null
                                        || plan.isBlank()
                                        || plan.equalsIgnoreCase(payment.plan))

                                &&

                                (status == null
                                        || status.isBlank()
                                        || status.equalsIgnoreCase(payment.paymentStatus))

                                &&

                                (fromDate == null
                                        || (payment.paymentDate != null && !payment.paymentDate.isBefore(fromDate)))

                                &&

                                (toDate == null
                                        || (payment.paymentDate != null && !payment.paymentDate.isAfter(toDate))))

                        .sorted(
                                Comparator.comparing(
                                        payment -> payment.paymentDate,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder())))
                        .toList();

        return page(all, page, size);
    }

    @Override
    public Map<String, Object> revenue() {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime today =
                now.toLocalDate().atStartOfDay();

        LocalDateTime month =
                today.withDayOfMonth(1);

        LocalDateTime week =
                today.minusDays(
                        today.getDayOfWeek().getValue() - 1);

        List<Payment> successfulPayments =
                payments.findAll()
                        .stream()
                        .filter(payment ->
                                payment.getPaymentStatus()
                                        == PaymentStatus.SUCCESS)
                        .toList();

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "totalRevenue",
                sum(successfulPayments));

        response.put(
                "monthlyRevenue",
                sum(successfulPayments
                        .stream()
                        .filter(payment ->
                                payment.getPaymentDate() != null && !payment.getPaymentDate()
                                        .isBefore(month))
                        .toList()));

        response.put(
                "todayRevenue",
                sum(successfulPayments
                        .stream()
                        .filter(payment ->
                                payment.getPaymentDate() != null && !payment.getPaymentDate()
                                        .isBefore(today))
                        .toList()));

        response.put(
                "activeSubscriptions",
                subscriptions.findAll()
                        .stream()
                        .filter(subscription ->
                                subscription.getStatus()
                                        == SubscriptionStatus.ACTIVE)
                        .count());

        response.put(
                "expiredSubscriptions",
                subscriptions.findAll()
                        .stream()
                        .filter(subscription ->
                                subscription.getStatus()
                                        == SubscriptionStatus.EXPIRED)
                        .count());

        for (String plan : subscriptions.findAll().stream()
                .map(Subscription::getPlan).filter(Objects::nonNull).distinct().toList()) {

            response.put(
                    plan.toLowerCase() + "Plans",

                    subscriptions.findAll()
                            .stream()
                            .filter(subscription ->
                                    plan.equalsIgnoreCase(subscription.getPlan()))
                            .count());
        }

        response.put(
                "renewalsToday",

                successfulPayments.stream()
                        .filter(payment ->
                                payment.getPaymentDate() != null && !payment.getPaymentDate()
                                        .isBefore(today))
                        .count());

        response.put(
                "renewalsThisWeek",

                successfulPayments.stream()
                        .filter(payment ->
                                payment.getPaymentDate() != null && !payment.getPaymentDate()
                                        .isBefore(week))
                        .count());

        response.put(
                "paymentsToday",

                successfulPayments.stream()
                        .filter(payment ->
                                payment.getPaymentDate() != null && !payment.getPaymentDate()
                                        .isBefore(today))
                        .count());

        response.put(
                "paymentsThisMonth",

                successfulPayments.stream()
                        .filter(payment ->
                                payment.getPaymentDate() != null && !payment.getPaymentDate()
                                        .isBefore(month))
                        .count());

        return response;
    }

        private AdminSubscriptionDTO map(Subscription subscription) {

        AdminSubscriptionDTO dto =
                new AdminSubscriptionDTO();

        Organization organization =
                organizations.findById(
                        subscription.getOrganizationId())
                        .orElse(null);

        User owner =
                users.findById(
                        subscription.getOwnerId())
                        .orElse(null);

        Payment latestPayment =
                payments.findByOrganizationIdOrderByPaymentDateDesc(
                                subscription.getOrganizationId())
                        .stream()
                        .findFirst()
                        .orElse(null);

        dto.id = subscription.getId();
        dto.organizationId = subscription.getOrganizationId();
        dto.organizationName =
                organization == null
                        ? "Unknown"
                        : organization.getName();

        dto.ownerId = subscription.getOwnerId();
        dto.ownerName =
                owner == null
                        ? "Unknown"
                        : owner.getName();

        dto.ownerEmail =
                owner == null
                        ? "-"
                        : owner.getEmail();

        dto.plan = subscription.getPlan();
        dto.status = name(subscription.getStatus());

        dto.startDate = subscription.getStartDate();
        dto.expiryDate = subscription.getEndDate();

        dto.projectLimit = subscription.getProjectLimit();
        dto.memberLimit = subscription.getMemberLimit();
        dto.issueLimit = subscription.getIssueLimit();

        dto.currentProjects = subscription.getCurrentProjects();
        dto.currentMembers = subscription.getCurrentMembers();
        dto.currentIssues = subscription.getCurrentIssues();

        dto.daysRemaining =
                subscription.getEndDate() == null
                        ? null
                        : Math.max(
                                0,
                                Duration.between(
                                        LocalDateTime.now(),
                                        subscription.getEndDate())
                                        .toDays());

        if (latestPayment != null) {

            dto.paymentStatus =
                    name(latestPayment.getPaymentStatus());

            dto.amount =
                    latestPayment.getAmount();

            dto.currency =
                    latestPayment.getCurrency();

            dto.transactionId =
                    latestPayment.getTransactionId();

            dto.paymentMethod =
                    name(latestPayment.getPaymentMethod());

            dto.lastPaymentDate =
                    latestPayment.getPaymentDate();
        }

        dto.paymentHistory = payments.findByOrganizationIdOrderByPaymentDateDesc(subscription.getOrganizationId())
                .stream().map(this::payment).toList();

        return dto;
    }

    private AdminPaymentDTO payment(Payment payment) {

        AdminPaymentDTO dto =
                new AdminPaymentDTO();

        Organization organization =
                organizations.findById(
                        payment.getOrganizationId())
                        .orElse(null);

        User owner =
                users.findById(
                        payment.getOwnerId())
                        .orElse(null);

        dto.id = payment.getId();

        dto.organizationId =
                payment.getOrganizationId();

        dto.organizationName =
                organization == null
                        ? "Unknown"
                        : organization.getName();

        dto.ownerId =
                payment.getOwnerId();

        dto.ownerName =
                owner == null
                        ? "Unknown"
                        : owner.getName();

        dto.plan = payment.getPlan();

        dto.amount =
                payment.getAmount();

        dto.currency =
                payment.getCurrency();

        dto.paymentStatus =
                name(payment.getPaymentStatus());

        dto.paymentMethod =
                name(payment.getPaymentMethod());

        dto.transactionId =
                payment.getTransactionId();

        dto.paymentDate =
                payment.getPaymentDate();

        return dto;
    }

    private boolean matches(
            AdminSubscriptionDTO dto,
            String search,
            String plan,
            String status,
            String paymentStatus) {

        return

                (search == null
                        || search.isBlank()
                        || contains(dto.organizationName, search)
                        || contains(dto.ownerName, search)
                        || contains(dto.ownerEmail, search))

                &&

                (plan == null
                        || plan.isBlank()
                        || plan.equalsIgnoreCase(dto.plan))

                &&

                (status == null
                        || status.isBlank()
                        || status.equalsIgnoreCase(dto.status))

                &&

                (paymentStatus == null
                        || paymentStatus.isBlank()
                        || paymentStatus.equalsIgnoreCase(
                                dto.paymentStatus));
    }

    private boolean contains(
            String value,
            String keyword) {

        return value != null
                && value.toLowerCase()
                        .contains(keyword.toLowerCase());
    }

    private LocalDateTime parse(
            String value,
            boolean endOfDay) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value).atTime(endOfDay ? LocalTime.MAX : LocalTime.MIN);
        } catch (java.time.format.DateTimeParseException ex) {
            throw new com.bugflow.exception.BadRequestException("Invalid date format. Use yyyy-MM-dd");
        }
    }

    private java.math.BigDecimal sum(
            List<Payment> payments) {

        return payments.stream()
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(
                        java.math.BigDecimal.ZERO,
                        java.math.BigDecimal::add);
    }

    private String name(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private <T> PageResponseDTO<T> page(
            List<T> data,
            int page,
            int size) {

        int start =
                Math.max(0, page)
                        * Math.max(1, size);

        int end =
                Math.min(
                        data.size(),
                        start + Math.max(1, size));

        return new PageResponseDTO<>(
                start >= data.size()
                        ? List.of()
                        : data.subList(start, end),
                Math.max(0, page),
                Math.max(1, size),
                data.size());
    }
}
