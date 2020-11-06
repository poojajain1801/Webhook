/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
