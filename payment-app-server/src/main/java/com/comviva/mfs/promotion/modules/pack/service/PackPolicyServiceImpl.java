package com.comviva.mfs.promotion.modules.pack.service;

import com.comviva.mfs.promotion.exception.ValidationException;
import com.comviva.mfs.promotion.model.error.PropertyErrors;
import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;
import com.comviva.mfs.promotion.modules.pack.model.PackConfiguration;
import com.comviva.mfs.promotion.modules.pack.repository.PackPolicyRepository;
import com.comviva.mfs.promotion.modules.pack.repository.PackPolicyCustomRepository;
import com.comviva.mfs.promotion.modules.pack.service.contract.PackPolicyService;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by sumit.das on 12/26/2016.
 */
@Service
@Getter
@ToString
@EqualsAndHashCode
public class PackPolicyServiceImpl implements PackPolicyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackPolicyServiceImpl.class);
    private final PackPolicyRepository packPolicyRepository;
    private final PackPolicyCustomRepository packPolicyCustomRepository;

    @Autowired
    public PackPolicyServiceImpl(PackPolicyRepository packPolicyRepository, PackPolicyCustomRepository packPolicyCustomRepository) {
        this.packPolicyRepository = packPolicyRepository;
        this.packPolicyCustomRepository = packPolicyCustomRepository;
    }

    @Override
    public PackPolicy saveOrUpdate(PackPolicy packPolicy) {
        LOGGER.debug("Validating pack policy of type {}", packPolicy.getType());
        PropertyErrors errors = packPolicy.validate(new PackConfiguration());
        if (errors.hasErrors()) {
            LOGGER.error("Error during saving pack policy : {}, errors: {}", packPolicy, errors);
            throw new ValidationException("Error during saving policy", errors);
        }
        return packPolicyCustomRepository.updateOrSave(packPolicy);
    }

    @Override
    public PackPolicy getPackPolicyBasedOnType(String type) {
        Optional<PackPolicy> policyDBData = packPolicyRepository.findByType(type);
        return policyDBData.isPresent()? policyDBData.get() : null;
    }
}
