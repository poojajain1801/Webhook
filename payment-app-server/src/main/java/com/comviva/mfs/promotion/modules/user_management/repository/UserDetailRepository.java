package com.comviva.mfs.promotion.modules.user_management.repository;

import com.comviva.mfs.promotion.modules.user_management.domain.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, String>{
    Optional<UserDetail> findByUserName(String userName);

}
