package com.comviva.mfs.hce.appserver.repository;


import com.comviva.mfs.hce.appserver.model.VisaCardDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Amgoth.madan on 5/15/2017.
 */
@Repository
public interface VisaCardDetailRepository extends JpaRepository<VisaCardDetails, String>{
}
