package com.comviva.mfs.hce.appserver.util.vts;

import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollDeviceRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RegDeviceParam;
import com.comviva.mfs.hce.appserver.mapper.vts.EnrollDevice;
import com.visa.cbp.encryptionutils.common.EncDevicePersoData;
import com.visa.cbp.encryptionutils.map.VisaSDKMapUtil;
import lombok.Setter;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.GeneralSecurityException;

/** Send enroll device request to VTS */
// 1. Prepare Device Info
// 2. Prepare VTS Certificates
//       a) i) vCertificateID
//         ii) certUsage - CONFIDENTIALITY
//       b) i) vCertificateID
//         ii) certUsage - INTEGRITY
// 3. Generate Device Encryption Key Pair
//          i) certUsage - CONFIDENTIALITY
//         ii) certFormat - X509
//        iii) certValue -
// 4. Generate Device Signature Key Pair
//          i) certUsage - INTEGRITY
//         ii) certFormat - X509
//        iii) certValue -
// 5. Prepare EnrollDevice request and send it to VTS

/** Send response to Mobile Application */
// 1. Fetch clientDeviceID from VTS response
// 2. Fetch vClientID from VTS response
// 3. Fetch vServerNonce from VTS response
// 4. Prepare VTS Encryption Public Key
// 5. Prepare VTS Signature Public Key
// 6. Prepare Device Encryption Private Key
// 7. Prepare Device Signature Private Key
// 8. Send response to Mobile Application

@Setter
public class EnrollDeviceVts {
    private Environment env;
    private EncDevicePersoData encDevicePersoData;
    private EnrollDeviceRequest enrollDeviceRequest;

    public EncDevicePersoData getEncDevicePersoData() {
        return encDevicePersoData;
    }

   // public ResponseEntity<EnrollDeviceResponse> register(final String vClientID) {
     public String register(final String vClientID) {
        // Enroll device with VTS
        EnrollDevice enrollDevice = new EnrollDevice(env);
        enrollDevice.setVClientID(vClientID);
        //ResponseEntity<EnrollDeviceResponse> response = null;
        String response="";
        try {
            response = enrollDevice.enrollDevice(enrollDeviceRequest.getVts().getDeviceInfo());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
       // EnrollDeviceResponse enrollDevResp = response.getBody();
        encDevicePersoData = VisaSDKMapUtil.getEncryptedDevicePersoData(enrollDevice.getDevicePersoData());

        return response;
    }

    private void enrollDevice() {

    }

}
