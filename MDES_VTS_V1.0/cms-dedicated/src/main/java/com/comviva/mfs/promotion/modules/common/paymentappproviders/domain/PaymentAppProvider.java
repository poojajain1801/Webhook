package com.comviva.mfs.promotion.modules.common.paymentappproviders.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "PAYMENT_APP_PROVIDER_LIST")
@ToString
@EqualsAndHashCode
public class PaymentAppProvider {
    @Id
    @Column(name = "id")
    private final int id;

    @Column(name = "payment_app_provider_id")
    private final String paymentAppProviderId;

    @Column(name = "payment_app_provider_name")
    private final String paymentAppProviderName;

    public PaymentAppProvider(int id,
                              String paymentAppProviderId,
                              String paymentAppProviderName) {
        this.id = id;
        this.paymentAppProviderId = paymentAppProviderId;
        this.paymentAppProviderName = paymentAppProviderName;
    }

    public PaymentAppProvider() {
        this(0, null, null);
    }

}
