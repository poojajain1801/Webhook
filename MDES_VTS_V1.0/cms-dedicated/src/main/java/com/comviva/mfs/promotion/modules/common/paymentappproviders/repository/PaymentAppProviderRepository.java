package com.comviva.mfs.promotion.modules.common.paymentappproviders.repository;

import com.comviva.mfs.promotion.modules.common.paymentappproviders.domain.PaymentAppProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PAYMENT_APP_PROVIDER_LIST repository.
 */
@Repository
public interface PaymentAppProviderRepository extends JpaRepository<PaymentAppProvider, String>{
    Optional<PaymentAppProvider> findByPaymentAppProviderId(String paymentAppProviderId);

}
