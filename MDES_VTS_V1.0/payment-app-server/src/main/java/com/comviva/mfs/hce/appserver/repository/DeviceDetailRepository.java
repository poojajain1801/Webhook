package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Repository
public interface DeviceDetailRepository extends JpaRepository<DeviceInfo, String>{
    Optional<DeviceInfo> findByImei(String imei);
    Optional<DeviceInfo> findByPaymentAppInstanceId(String payment_app_instance_id);

}
