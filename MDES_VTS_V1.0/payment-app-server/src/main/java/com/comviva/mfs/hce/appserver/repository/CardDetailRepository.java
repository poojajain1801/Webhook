package com.comviva.mfs.hce.appserver.repository;

import com.comviva.mfs.hce.appserver.model.CardDetails;

import com.comviva.mfs.hce.appserver.model.VisaCardDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public interface CardDetailRepository extends JpaRepository<CardDetails, String>{

    @Query("Select vc from CardDetails vc where vc.deviceInfo.clientDeviceId=:clientDeviceId and vc.panUniqueReference=:panUniqueReference")
    List<CardDetails> findByPanUniqueReferenceAndClientDeviceId(@Param("panUniqueReference")String panUniqueReference,@Param("clientDeviceId")String clientDeviceId);
    List<CardDetails> findByPanUniqueReference(String panUniqueReference);
    List<CardDetails> findByVisaProvisionTokenId(String visaProvisionTokenId);
    @Query("Select vc from CardDetails vc where vc.cardIdentifier =:cardIdentifier and vc.deviceInfo.userDetail.clientWalletAccountId=:clientWalletAccountId and vc.deviceInfo.clientDeviceId=:clientDeviceId and vc.status in(:active,:suspend)")
    List<CardDetails> findCardDetailsByIdentifier(@Param("cardIdentifier") String cardIdentifier, @Param("clientWalletAccountId")String clientWalletAccountId, @Param("clientDeviceId")String clientDeviceId, @Param("active") String active,@Param("suspend") String suspend);
    Optional<CardDetails> findByMasterTokenUniqueReference(String masterTokenUniqueReference);
    Optional<CardDetails> findByMasterPaymentAppInstanceIdAndMasterTokenUniqueReference(String masterPaymentAppInstanceId, String masterTokenUniqueReference);

    @Query("Select vc from CardDetails vc where vc.deviceInfo.userDetail.userId=:userId and vc.deviceInfo.status=:deviceStatus and vc.status in (:active,:suspend) order by vc.createdOn DESC")
    List<CardDetails> getCardList(@Param("userId") String userId, @Param("deviceStatus")String status, @Param("active") String active, @Param("suspend") String suspend,Pageable pageable);

    @Query("Select vc from CardDetails vc where vc.deviceInfo.userDetail.userId=:userId and vc.deviceInfo.status=:deviceStatus and vc.status in (:active,:suspend) order by vc.createdOn DESC")
    List<CardDetails> getCardList(@Param("userId") String userId, @Param("deviceStatus")String status, @Param("active") String active, @Param("suspend") String suspend);


    default List<CardDetails> getNCardList(String userId,String deviceStatus,String active,String suspend,int page, int size) {
        if(size==0){
            return getCardList(userId,deviceStatus,active,suspend);
        }else{
            return getCardList(userId,deviceStatus,active,suspend, new PageRequest(page,size));
        }

    }

}
