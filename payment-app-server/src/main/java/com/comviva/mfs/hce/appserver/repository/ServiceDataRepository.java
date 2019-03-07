package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.ServiceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Tanmay.Patel on 2/2/2017.
 */
@Repository
public interface ServiceDataRepository extends JpaRepository<ServiceData,String>{

    Optional<ServiceData> findByServiceId(String serviceId);

}
