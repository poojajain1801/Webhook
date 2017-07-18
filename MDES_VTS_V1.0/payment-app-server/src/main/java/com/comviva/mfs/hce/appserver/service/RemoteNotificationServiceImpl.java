package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.hce.appserver.mapper.pojo.RemoteNotificationRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.repository.DeviceDetailRepository;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RemoteNotification;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsFactory;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsGenericRequest;
import com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm.RnsResponse;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
public class RemoteNotificationServiceImpl implements com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService {
    private final DeviceDetailRepository deviceDetailRepository;

    @Autowired
    public RemoteNotificationServiceImpl(DeviceDetailRepository deviceDetailRepository) {
        this.deviceDetailRepository = deviceDetailRepository;
    }

    public Map sendRemoteNotificationMessage(RemoteNotificationRequest remoteNotificationReq) {
        // Create Remote Notification Data
        RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM);

        // Fetch RNS RegistrationID
        Optional<DeviceInfo> oDeviceInfo = deviceDetailRepository.findByPaymentAppInstanceId(remoteNotificationReq.getPaymentAppInstanceId());
        if(!oDeviceInfo.isPresent()) {
            return ImmutableMap.of("errorCode", "202", "errorDescription", "Incorrect PaymentAppInstanceID");
        }

        String rnsRegId = oDeviceInfo.get().getRnsId();

        byte[] rnsPostData = Base64.getDecoder().decode(remoteNotificationReq.getNotificationData().getBytes());

        RnsResponse response = rns.sendRns(rnsRegId, rnsPostData);
        if (Integer.valueOf(response.getErrorCode()) != 200) {
            return ImmutableMap.of("errorCode", "234",
                    "errorDescription", "RNS Unavailable");
        }
        return ImmutableMap.of("responseHost", "localhost",
                "responseId", "12345678");
    }

    public Map sendRemoteNotification(RnsGenericRequest rnsGenericRequest) {
        // Create Remote Notification Data
        RemoteNotification rns = RnsFactory.getRnsInstance(RnsFactory.RNS_TYPE.FCM);

        // TODO fetch RNS RegistrationID using id as paymentAppServerId or clientDeviceId
        String rnsRegId = null;

        byte[] rnsPostData = Base64.getDecoder().decode(rnsGenericRequest.getRnsData().getBytes());

        RnsResponse response = rns.sendRns(rnsRegId, rnsPostData);
        if (Integer.valueOf(response.getErrorCode()) != 200) {
            // TODO
               /* return new RequestSessionResp(Integer.toString(ConstantErrorCodes.RNS_UNAVAILABLE),
                        ConstantErrorCodes.errorCodes.get(ConstantErrorCodes.RNS_UNAVAILABLE));*/
        }
        return null;
    }
}
