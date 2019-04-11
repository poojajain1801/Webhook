package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.CardDetails;
import com.comviva.mfs.hce.appserver.model.ConfigurationManagementM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
@Repository
public interface ConfigurationManagementMRepository extends JpaRepository<ConfigurationManagementM, String> {

    List<ConfigurationManagementM> findByStatus(String Status);
    ConfigurationManagementM findByRequestId(String requestId);
}
