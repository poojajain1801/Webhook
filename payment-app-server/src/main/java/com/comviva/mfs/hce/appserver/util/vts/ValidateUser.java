package com.comviva.mfs.hce.appserver.util.vts;

import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollPanRequest;
import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.comviva.mfs.hce.appserver.model.UserDetail;
import com.comviva.mfs.hce.appserver.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amgoth.naik on 6/29/2017.
 */


public class ValidateUser {
    private final UserDetailRepository userDetailRepository;

    @Autowired
    public ValidateUser(UserDetailRepository userDetailRepository) {
            this.userDetailRepository=userDetailRepository;
    }

    public Map<String,Object> validate(String clientDeviceID, List<UserDetail> userDetails, List<DeviceInfo> deviceInfo) {


        /*
        Map<String,Object> result=new HashMap();
        if ((null==userDetails || userDetails.isEmpty()) || (null==deviceInfo || deviceInfo.isEmpty())) {
            result.put("message", "Invalid User please register");
            result.put("responseCode", "205");
            return result;
        }else if("userActivated".equals(userDetails.get(0).getUserStatus()) && "deviceActivated".equals(deviceInfo.get(0).getDeviceStatus())){
            List<UserDetail> userDevice = userDetailRepository.findByClientDeviceId(clientDeviceID);
            if(null !=userDevice && !userDevice.isEmpty()) {
                for (int i = 0; i <userDetails.size(); i++){
                    if (!userDevice.get(i).getUserName().equals(userDetails.get(0).getUserName())) {
                        userDevice.get(i).setClientDeviceId("CD");
                        userDetailRepository.save(userDevice.get(i));
                    }
                }
            }
            userDetails.get(0).setClientDeviceId(clientDeviceID);
            userDetailRepository.save(userDetails.get(0));
            result.put("message", "Active User");
            result.put("responseCode", "200");
            return result;
        }else{
            result.put("message", "User not active");
            result.put("responseCode", "205");
            return result;
        }
    */
        return new HashMap();

    }
}