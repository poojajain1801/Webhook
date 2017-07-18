package com.comviva.mfs.promotion.modules.common.sessionmanagement.repository;

import com.comviva.mfs.promotion.modules.common.sessionmanagement.domain.PendingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingTaskRepository extends JpaRepository<PendingTask, String> {
    Optional<PendingTask> findByPaymentAppInstanceId(String paymentAppInstanceId);

    Optional<PendingTask> findByPaymentAppInstanceIdAndTokenUniqueReference(String paymentAppInstanceId, String tokenUniqueReference);
    Optional<PendingTask> findByPaymentAppInstanceIdAndStatus(String paymentAppInstanceId, String status);
    Optional<PendingTask> findByPaymentAppInstanceIdAndTokenUniqueReferenceAndStatus(String paymentAppInstanceId, String tokenUniqueReference, String status);
}
