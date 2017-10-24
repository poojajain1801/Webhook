package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, String>{
    Optional<UserDetail> findByUserName(String userName);

    List<UserDetail>findByUserIdandStatus(String userId,String stataus);

    @Query("Select distinct mp from UserDetail mp where mp.clientDeviceId =:clientDeviceId")
    List<UserDetail> findByClientDeviceId(@Param("clientDeviceId")String clientDeviceId);

}
