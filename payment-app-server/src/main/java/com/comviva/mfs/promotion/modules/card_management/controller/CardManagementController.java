package com.comviva.mfs.promotion.modules.card_management.controller;

import com.comviva.mfs.promotion.modules.card_management.model.*;
import com.comviva.mfs.promotion.modules.card_management.service.contract.CardDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@RestController
@RequestMapping("/api/card")
public class CardManagementController {

    @Autowired
    private CardDetailService cardDetailService;

    public CardManagementController(CardDetailService cardDetailService ) {
        this.cardDetailService = cardDetailService;
    }

    @ResponseBody
    @RequestMapping(value = "/checkCardEligibility", method = RequestMethod.POST)
    public AddCardResponse addCard(@RequestBody AddCardParm addCardParm) {
        return cardDetailService.checkDeviceEligibility(addCardParm);
    }

    @ResponseBody
    @RequestMapping(value = "/continueDigitization", method = RequestMethod.POST)
    public AddCardResponse continueDigitization(@RequestBody DigitizationParam digitizationParam) {
        return cardDetailService.addCard(digitizationParam);
    }

    @RequestMapping(value = "/mdes/digitization/1/0/asset", method = RequestMethod.GET)
    public Asset getAsset(@RequestParam(value="assetId") String assetId) {
        return cardDetailService.getAsset(assetId);
    }

    @ResponseBody
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    public ActivateResp activate(@RequestBody ActivateReq activateReq) {
        return cardDetailService.activate(activateReq);
    }
}

