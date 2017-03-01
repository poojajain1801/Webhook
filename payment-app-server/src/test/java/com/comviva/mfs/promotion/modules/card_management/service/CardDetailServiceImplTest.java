package com.comviva.mfs.promotion.modules.card_management.service;

import com.comviva.mfs.promotion.modules.card_management.model.AddCardParm;
import com.comviva.mfs.promotion.modules.card_management.model.AddCardResponse;
import com.comviva.mfs.promotion.modules.card_management.model.CardInfo;
import com.comviva.mfs.promotion.modules.card_management.model.DigitizationParam;
import com.comviva.mfs.promotion.modules.card_management.service.contract.CardDetailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by Tanmay.Patel on 2/2/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class CardDetailServiceImplTest {


    @Autowired
    CardDetailService cardDetailService;
    CardInfo cardInfo = new CardInfo("","","4c4ead5927f0df8117f178eea9308daa58e27c2b","A1B2C3D4E5F6112233445566","SHA512","","4545433044323232363739304532433610DE1D1461475BEB6D815F31764DDC20298BD779FBE37EE5AB3CBDA9F9825E1DDE321469537FE461E824AA55BA67BF6A");
    AddCardParm addCardParm = new AddCardParm("addcard","CLOUD","123456789","WalletApp1",cardInfo,"1.0","en","","");
    DigitizationParam digitizationParam = new DigitizationParam("addcard","81d9f8e0-6292-11e3-949a-0800200c9a66","2014-07-04T12:08:56.123-07:00", "123456789");
    @Test
    public void checkDeviceEligibility() throws Exception {

        AddCardResponse addCardResponse= cardDetailService.checkDeviceEligibility(addCardParm);
        System.out.println("checkDeviceEligibility Response="+ addCardResponse);
    }
    @Test
    public void addCard() throws Exception {
        AddCardResponse addCardResponse = cardDetailService.addCard(digitizationParam);
    }

}