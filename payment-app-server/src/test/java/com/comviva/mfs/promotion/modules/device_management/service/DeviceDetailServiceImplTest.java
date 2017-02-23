package com.comviva.mfs.promotion.modules.device_management.service;

import com.comviva.mfs.promotion.modules.device_management.domain.DeviceInfo;
import com.comviva.mfs.promotion.modules.device_management.model.DeviceRegistrationResponse;
import com.comviva.mfs.promotion.modules.device_management.model.RegDeviceParam;
import com.comviva.mfs.promotion.modules.device_management.service.contract.DeviceDetailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertThat;

/**
 * Created by Tanmay.Patel on 1/17/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceDetailServiceImplTest {

    @Autowired
    DeviceDetailService deviceDetailService;

    private DeviceInfo devieceDetail = new DeviceInfo("352099001761481", "ANDROID", "4.4", "2F6D63", "PHONE", "CLOUD", "YES", "352099001761481", "8861329101", "MYPHONE");

    private RegDeviceParam regDeviceParam = new RegDeviceParam("tanmaya", "40", "4BE25E93B9CDE42CC6DD5EA826456812", "WalletApp1", "123456789", "4c4ead5927f0df8117f178eea9308daa58e27c2b", "3827C26D78B71E6F281B40AB69F62AEFC27E25E5C7FADFA5A8FBE7646E3D1362F97C9D419131F810A6739F1D2627ECF07524579FDB38423746DB138F816D09E9CDEA26712661A49631531514F769635486A5CFE9380FE1FEC9BE744DD8F7142B1586C1EF60539BF557BC799DFB785B4093A6C890E0C590BC2A0C81DE2F29889A", "APA91bHPRgkF3JUikC4ENAHEeMrd41Zxv3hVZjC9KtT8OvPVGJ-hQMRKRrZuJAEcl7B338qju59zJMjw2DELjzEvxwYv7hH5Ynpc1ODQ0aT4U4OFEeco8ohsN5PjL1iC2dNtk2BAokeMCg2ZXKqpc8FXKmhX94kIxQ", devieceDetail);


    @Test
    public void registerDeviece() throws Exception {
        DeviceRegistrationResponse response = deviceDetailService.registerDeviece(regDeviceParam);
        System.out.println("response =" + response.toString());
    }

    @Test
    public void checkDeviceEligibility() throws Exception {


        boolean response = deviceDetailService.checkDeviceEligibility(regDeviceParam);
        System.out.println("response =" + response);
    }

    @Test
    public void registerdeviceWithCMSD() throws Exception {
        String resonse = deviceDetailService.registerDeviceWithCMSD(regDeviceParam);

    }


}
