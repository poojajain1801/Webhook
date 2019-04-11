package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.ConfigurationManagement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
public interface ConfigurationManagementRepository extends JpaRepository<ConfigurationManagement, String> {
}
