package com.comviva.mfs.hce.appserver.repository;


import com.comviva.mfs.hce.appserver.model.VisaCardDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Amgoth.madan on 5/15/2017.
 */
@Repository
public interface VisaCardDetailRepository extends JpaRepository<VisaCardDetails, String>{

   // List<VisaCardDetails> findByVPanEnrollmentId(String VPanEnrollmentId);
   // List<VisaCardDetails> findByVProvisionedTokenId(String VProvisionedtokenid);
    //@Query("Select vc from VisaCardDetails vc where vc.cardIdentifier =:cardIdentifier and vc.deviceInfo.userDetail.clientWalletAccountId=:clientWalletAccountId and vc.deviceInfo.clientDeviceId=:clientDeviceId and vc.status=:status")
   // List<VisaCardDetails> findCardDetailsByIdentifier(@Param("cardIdentifier") String cardIdentifier, @Param("clientWalletAccountId")String clientWalletAccountId, @Param("clientDeviceId")String clientDeviceId, @Param("status") String status);

}
