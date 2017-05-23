package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.ServiceData;
import com.comviva.mfs.hce.appserver.model.TransctionRegDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Tanmay.Patel on 5/19/2017.
 */
@Repository
public interface TransctionRegDetailsRepository extends JpaRepository<TransctionRegDetails,String> {
    Optional<TransctionRegDetails> findByTokenUniqueReference(String tokenUniqueReference);
}
