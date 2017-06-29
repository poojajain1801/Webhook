package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Repository
public interface DeviceDetailRepository extends JpaRepository<DeviceInfo, String>{

    @Query("Select distinct mp from DeviceInfo mp where mp.clientDeviceId =:clientDeviceId")
        //Map<String,Object> find(@Param("userName") String userName);
    List<DeviceInfo> find(@Param("clientDeviceId") String clientDeviceId);
    Optional<DeviceInfo> findByImei(String imei);
    Optional<DeviceInfo> findByPaymentAppInstanceId(String payment_app_instance_id);
    Optional<DeviceInfo> findByClientDeviceId(String clientDeviceId);

}
