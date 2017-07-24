package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.TransactionRegDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Tanmay.Patel on 5/19/2017.
 */
@Repository
public interface TransactionRegDetailsRepository extends JpaRepository<TransactionRegDetails,String> {
    Optional<TransactionRegDetails> findByTokenUniqueReference(String tokenUniqueReference);
    Optional<TransactionRegDetails> findByPaymentAppInstanceId(String paymentAppInstanceId);
}
