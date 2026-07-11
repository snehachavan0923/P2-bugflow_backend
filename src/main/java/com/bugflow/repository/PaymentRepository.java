package com.bugflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bugflow.entity.Payment;
import com.bugflow.entity.PaymentStatus;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByOwnerIdOrderByPaymentDateDesc(String ownerId);

    List<Payment> findByOrganizationIdOrderByPaymentDateDesc(String organizationId);

    List<Payment> findByPaymentStatusOrderByPaymentDateDesc(PaymentStatus paymentStatus);

    Optional<Payment> findByTransactionId(String transactionId);
}
