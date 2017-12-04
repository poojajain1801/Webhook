package com.comviva.mfs.hce.appserver.util.vts;

import com.comviva.mfs.hce.appserver.model.DeviceInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by amgoth.naik on 6/27/2017.
 */
public class CreateChannelSecurityContext {
    public Map<String, Object> visaChannelSecurityContext(List<DeviceInfo> deviceInfo){

        Map<String,Object> channelSecurityContext=new HashMap<>();
        Map<String,Object>vtsCerts=new HashMap<>();
        Map<String,Object>deviceCerts=new HashMap<>();
        Map<String,Object>channelInfo=new HashMap<>();
        Map<String,String>deviceCert1=new HashMap<>();
        Map<String,String>deviceCert2=new HashMap<>();
        Map<String,String>vtsCert1=new HashMap<>();
        Map<String,String>vtsCert2=new HashMap<>();
        Map<String,Object>finalMap=new HashMap<>();

        List<Map> vtsCertList=new LinkedList<>();
        List<Map> deviceCertsList=new LinkedList<>();

        channelInfo.put("encryptionScheme","RSA_PKI");

      /*  vtsCert1.put("vCertificateID",deviceInfo.get(0).getVtscerts_vcertificateid_confidentiality());
        vtsCert1.put("certUsage",deviceInfo.get(0).getVtscerts_certusage_confidentiality());

        vtsCert2.put("vCertificateID",deviceInfo.get(0).getVtscerts_vcertificateid_integrity());
        vtsCert2.put("certUsage",deviceInfo.get(0).getVtscerts_certusage_integrity());


        deviceCert1.put("certValue",deviceInfo.get(0).getDevicecerts_certvalue_confidentiality());
        deviceCert1.put("certUsage",deviceInfo.get(0).getDevicecerts_certusage_confidentiality());
        deviceCert1.put("certFormat",deviceInfo.get(0).getDevicecerts_certformat_confidentiality());



        deviceCert2.put("certValue",deviceInfo.get(0).getDevicecerts_certvalue_integrity());
        deviceCert2.put("certUsage",deviceInfo.get(0).getDevicecerts_certusage_integrity());
        deviceCert2.put("certFormat",deviceInfo.get(0).getDevicecerts_certformat_integrity());*/

        deviceCerts.put("deviceCert",deviceCert1);
        deviceCerts.put("deviceCert",deviceCert2);

        vtsCerts.put("vtsCert",vtsCert1);
        vtsCerts.put("vtsCert",vtsCert2);

        vtsCertList.add(0,vtsCert1);
        vtsCertList.add(0,vtsCert2);


        deviceCertsList.add(0,deviceCert1);
        deviceCertsList.add(0,deviceCert2);

        channelSecurityContext.put("vtsCerts",vtsCertList);
        channelSecurityContext.put("deviceCerts",deviceCertsList);
        channelSecurityContext.put("channelInfo",channelInfo);

       // finalMap.put("channelSecurityContext",channelSecurityContext);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValueAsString(channelSecurityContext);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return channelSecurityContext;

    }
}
