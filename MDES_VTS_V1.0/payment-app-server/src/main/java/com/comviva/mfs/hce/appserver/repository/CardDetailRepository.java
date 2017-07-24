package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.CardDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Repository
public interface CardDetailRepository extends JpaRepository<CardDetails, String>{
    Optional<CardDetails> findByTokenUniqueReference(String tokenUniqueReference);
    Optional<CardDetails> findByPaymentAppInstanceIdAndTokenUniqueReference(String paymentAppInstanceId, String tokenUniqueReference);

}
