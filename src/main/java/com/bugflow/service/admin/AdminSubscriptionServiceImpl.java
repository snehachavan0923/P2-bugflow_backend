package com.bugflow.service.admin;

import java.math.BigDecimal;
import org.bson.types.Decimal128;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.bugflow.dto.admin.AdminPaymentDTO;
import com.bugflow.dto.admin.AdminRevenueDashboardDTO;
import com.bugflow.dto.admin.AdminSubscriptionDTO;
import com.bugflow.dto.admin.PageResponseDTO;
import com.bugflow.dto.admin.PlanCountDTO;
import com.bugflow.dto.admin.RevenueValueDTO;
import com.bugflow.dto.admin.RenewalsTrendDTO;
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
    private final MongoTemplate mongoTemplate;

    public AdminSubscriptionServiceImpl(
            SubscriptionRepository subscriptions,
            PaymentRepository payments,
            OrganizationRepository organizations,
            UserRepository users,
            MongoTemplate mongoTemplate) {

        this.subscriptions = subscriptions;
        this.payments = payments;
        this.organizations = organizations;
        this.users = users;
        this.mongoTemplate = mongoTemplate;
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
    public AdminRevenueDashboardDTO revenue(
            String from,
            String to,
            String plan) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.toLocalDate().atStartOfDay();
        LocalDateTime monthStart = today.withDayOfMonth(1);
        LocalDateTime weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);

        LocalDateTime fromDate = parse(from, false);
        LocalDateTime toDate = parse(to, true);

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new com.bugflow.exception.BadRequestException("From date cannot be after to date");
        }

        Criteria matchCriteria = Criteria.where("paymentStatus").is(PaymentStatus.SUCCESS);

        if (fromDate != null) {
            matchCriteria = matchCriteria.and("paymentDate").gte(fromDate);
        }

        if (toDate != null) {
            matchCriteria = matchCriteria.and("paymentDate").lte(toDate);
        }

        if (plan != null && !plan.isBlank()) {
            matchCriteria = matchCriteria.and("plan").is(plan);
        }

        Query revenueQuery = new Query(matchCriteria);

        List<Payment> successfulPayments =
                mongoTemplate.find(revenueQuery, Payment.class);

        AdminRevenueDashboardDTO dto = new AdminRevenueDashboardDTO();
        dto.totalRevenue = sum(successfulPayments);
        dto.monthlyRevenue = sum(filterByDate(successfulPayments, monthStart, null));
        dto.todayRevenue = sum(filterByDate(successfulPayments, today, null));
        dto.activeSubscriptions = subscriptions.countByStatus(SubscriptionStatus.ACTIVE);
        dto.expiredSubscriptions = subscriptions.countByStatus(SubscriptionStatus.EXPIRED);
        
        // Count plan totals directly from Subscription collection
        dto.freePlanCount = mongoTemplate.count(
                new Query(Criteria.where("plan").is("FREE")), 
                Subscription.class);
        dto.starterPlanCount = mongoTemplate.count(
                new Query(Criteria.where("plan").is("STARTER")), 
                Subscription.class);
        dto.businessPlanCount = mongoTemplate.count(
                new Query(Criteria.where("plan").is("BUSINESS")), 
                Subscription.class);
        dto.enterprisePlanCount = mongoTemplate.count(
                new Query(Criteria.where("plan").is("ENTERPRISE")), 
                Subscription.class);
        
        // Count payment statuses directly from Payment collection
        dto.successfulPaymentsCount = mongoTemplate.count(
                new Query(Criteria.where("paymentStatus").is(PaymentStatus.SUCCESS)), 
                Payment.class);
        dto.failedPaymentsCount = mongoTemplate.count(
                new Query(Criteria.where("paymentStatus").is(PaymentStatus.FAILED)), 
                Payment.class);
        
        dto.renewalsToday = filterByDate(successfulPayments, today, null).size();
        dto.renewalsThisWeek = filterByDate(successfulPayments, weekStart, null).size();
        dto.monthlyRevenueChart = buildMonthlyRevenueChart(fromDate, toDate, plan);
        dto.revenueByPlan = buildRevenueByPlan(fromDate, toDate, plan);
        dto.planDistribution = buildPlanDistribution(fromDate, toDate, plan);
        dto.renewalsTrend = buildRenewalsTrend(fromDate, toDate, plan);

        return dto;
    }

    private List<Payment> filterByDate(
            List<Payment> payments,
            LocalDateTime start,
            LocalDateTime end) {

        return payments.stream()
                .filter(payment -> payment.getPaymentDate() != null)
                .filter(payment -> !payment.getPaymentDate().isBefore(start))
                .filter(payment -> end == null || !payment.getPaymentDate().isAfter(end))
                .toList();
    }

    private List<RevenueValueDTO> buildMonthlyRevenueChart(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String plan) {

        LocalDate startDate = fromDate != null
                ? fromDate.toLocalDate().withDayOfMonth(1)
                : LocalDate.now().withDayOfMonth(1).minusMonths(5);

        LocalDate endDate = toDate != null
                ? toDate.toLocalDate()
                : LocalDate.now();

        // Fetch matching successful payments and aggregate in-memory to avoid
        // Mongo aggregation type/serialization mismatches for numeric sums.
        Query q = new Query(buildPaymentCriteria(fromDate, toDate, plan));
        List<Payment> matched = mongoTemplate.find(q, Payment.class);

        Map<String, BigDecimal> sums = new java.util.HashMap<>();
        for (Payment p : matched) {
            if (p.getPaymentDate() == null || p.getAmount() == null) continue;
            int y = p.getPaymentDate().getYear();
            int m = p.getPaymentDate().getMonthValue();
            String key = String.format("%04d-%02d", y, m);
            sums.put(key, sums.getOrDefault(key, BigDecimal.ZERO).add(p.getAmount()));
        }

        List<RevenueValueDTO> monthlySeries = new java.util.ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : sums.entrySet()) {
            monthlySeries.add(new RevenueValueDTO(e.getKey(), e.getValue()));
        }

        // Ensure series is filled for months with zero values and sorted
        return fillMonthlyChart(startDate, endDate, monthlySeries);
    }

    private List<RevenueValueDTO> buildRevenueByPlan(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String plan) {
                // Aggregate payments by plan in-memory to ensure numeric accuracy and
                // correct matching of SUCCESS payments.
                Query q = new Query(buildPaymentCriteria(fromDate, toDate, plan));
                List<Payment> matched = mongoTemplate.find(q, Payment.class);

                Map<String, BigDecimal> sums = new java.util.HashMap<>();
                for (Payment p : matched) {
                        if (p.getPlan() == null || p.getAmount() == null) continue;
                        String key = p.getPlan();
                        sums.put(key, sums.getOrDefault(key, BigDecimal.ZERO).add(p.getAmount()));
                }

                List<RevenueValueDTO> result = new java.util.ArrayList<>();
                sums.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .forEach(e -> result.add(new RevenueValueDTO(e.getKey(), e.getValue())));

                return result;
    }

    private List<PlanCountDTO> buildPlanDistribution(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String plan) {

        // Count subscriptions directly from the subscriptions collection
        // grouped by the `plan` field. Filter by plan param if provided and
        // only consider active subscriptions for distribution.
        Criteria c = Criteria.where("status").is(SubscriptionStatus.ACTIVE);
        if (plan != null && !plan.isBlank()) {
            c = c.and("plan").is(plan);
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(c),
                Aggregation.group("plan").count().as("count"),
                Aggregation.project("count").and("_id").as("plan"),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "plan")));

        return mongoTemplate.aggregate(aggregation, Subscription.class, PlanCountDTO.class).getMappedResults();
    }

    private List<RenewalsTrendDTO> buildRenewalsTrend(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String plan) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        LocalDate startDate = fromDate != null
                ? fromDate.toLocalDate()
                : LocalDate.now().minusDays(29);

        LocalDate endDate = toDate != null
                ? toDate.toLocalDate()
                : LocalDate.now();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(buildPaymentCriteria(fromDate, toDate, plan)),
                Aggregation.project("paymentDate")
                        .andExpression("year(paymentDate)").as("year")
                        .andExpression("month(paymentDate)").as("month")
                        .andExpression("dayOfMonth(paymentDate)").as("day"),
                Aggregation.group("year", "month", "day").count().as("renewals"),
                Aggregation.project("renewals").and("_id.year").as("year").and("_id.month").as("month").and("_id.day").as("day"),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "year", "month", "day")));

        List<Document> raw = mongoTemplate.aggregate(aggregation, Payment.class, Document.class).getMappedResults();
        List<RenewalsTrendDTO> trend = new java.util.ArrayList<>();

        for (Document doc : raw) {
            Integer year = toInteger(doc.get("year"));
            Integer monthValue = toInteger(doc.get("month"));
            Integer day = toInteger(doc.get("day"));
            Long renewals = toLong(doc.get("renewals"));
            if (year != null && monthValue != null && day != null) {
                String date = LocalDate.of(year, monthValue, day).format(formatter);
                trend.add(new RenewalsTrendDTO(date, renewals == null ? 0L : renewals));
            }
        }

        return fillRenewalsTrend(startDate, endDate, trend, formatter);
    }

    private Criteria buildPaymentCriteria(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String plan) {

        Criteria criteria = Criteria.where("paymentStatus").is(PaymentStatus.SUCCESS);

        if (fromDate != null) {
            criteria = criteria.and("paymentDate").gte(fromDate);
        }

        if (toDate != null) {
            criteria = criteria.and("paymentDate").lte(toDate);
        }

        if (plan != null && !plan.isBlank()) {
            criteria = criteria.and("plan").is(plan);
        }

        return criteria;
    }

    private List<RevenueValueDTO> fillMonthlyChart(
            LocalDate startDate,
            LocalDate endDate,
            List<RevenueValueDTO> grouped) {

        Map<String, BigDecimal> revenueByKey = grouped.stream()
                .filter(item -> item.label != null)
                .collect(
                        java.util.stream.Collectors.toMap(
                                item -> item.label,
                                item -> item.value == null ? BigDecimal.ZERO : item.value));

        List<RevenueValueDTO> result = new java.util.ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            String monthKey = String.format("%04d-%02d", current.getYear(), current.getMonthValue());
            result.add(new RevenueValueDTO(monthKey, revenueByKey.getOrDefault(monthKey, BigDecimal.ZERO)));
            current = current.plusMonths(1);
        }

        return result;
    }

    private List<RenewalsTrendDTO> fillRenewalsTrend(
            LocalDate startDate,
            LocalDate endDate,
            List<RenewalsTrendDTO> raw,
            DateTimeFormatter formatter) {

        Map<String, Long> trendByDate = raw.stream()
                .filter(item -> item.date != null)
                .collect(
                        java.util.stream.Collectors.toMap(
                                item -> item.date,
                                item -> item.renewals,
                                Long::sum));

        List<RenewalsTrendDTO> result = new java.util.ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            String dateKey = current.format(formatter);
            result.add(new RenewalsTrendDTO(dateKey, trendByDate.getOrDefault(dateKey, 0L)));
            current = current.plusDays(1);
        }

        return result;
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

        private BigDecimal toBigDecimal(Object value) {
                if (value == null) return null;
                if (value instanceof BigDecimal) return (BigDecimal) value;
                if (value instanceof Decimal128) return ((Decimal128) value).bigDecimalValue();
                if (value instanceof Number) {
                        Number n = (Number) value;
                        if (n instanceof Long || n instanceof Integer || n instanceof Short || n instanceof Byte) {
                                return BigDecimal.valueOf(n.longValue());
                        }
                        return BigDecimal.valueOf(n.doubleValue());
                }
                try {
                        return new BigDecimal(value.toString());
                } catch (Exception ex) {
                        return null;
                }
        }

        private Integer toInteger(Object value) {
                if (value == null) return null;
                if (value instanceof Number n) {
                        return n.intValue();
                }
                try {
                        return Integer.parseInt(value.toString());
                } catch (Exception ex) {
                        return null;
                }
        }

        private Long toLong(Object value) {
                if (value == null) return null;
                if (value instanceof Number n) {
                        return n.longValue();
                }
                try {
                        return Long.parseLong(value.toString());
                } catch (Exception ex) {
                        return null;
                }
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
