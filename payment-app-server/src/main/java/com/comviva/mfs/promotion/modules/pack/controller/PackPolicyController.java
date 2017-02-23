package com.comviva.mfs.promotion.modules.pack.controller;

import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;
import com.comviva.mfs.promotion.modules.pack.service.contract.PackPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by charu.sharma on 12/28/2016.
 */
@RestController
@RequestMapping("/api/packPolicy")
public class PackPolicyController {
    private final PackPolicyService packPolicyService;

    @Autowired
    public PackPolicyController(PackPolicyService packPolicyService) {
        this.packPolicyService = packPolicyService;
    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public PackPolicy savePackPolicy(@RequestBody PackPolicy packPolicy) {
        return packPolicyService.saveOrUpdate(packPolicy);
    }
}
