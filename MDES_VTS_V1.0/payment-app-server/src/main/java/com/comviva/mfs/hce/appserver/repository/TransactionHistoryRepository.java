package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.TransactionHistory;
import com.comviva.mfs.hce.appserver.model.TransctionRegDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Tanmay.Patel on 5/23/2017.
 */
@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory,String>{
    Optional<TransactionHistory> findByTokenUniqueReference(String tokenUniqueReference);
}
