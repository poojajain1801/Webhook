package com.comviva.mfs.promotion.modules.common.sessionmanagement.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name = "PENDING_TASK")
@ToString
@EqualsAndHashCode
public class PendingTask {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private final String id;

    @Column(name = "payment_app_instance_id")
    private String paymentAppInstanceId;

    @Column(name = "token_unique_reference")
    private String tokenUniqueReference;

    @Column(name = "action")
    private String action;

    @Column(name = "timestamp")
    private String timestamp;

    @Column(name = "status")
    private String status;

    public PendingTask() {
        this(null, null, null, null, null, null);
    }

    public PendingTask(String id, String paymentAppInstanceId, String tokenUniqueReference, String action, String timestamp, String status) {
        this.id = id;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.tokenUniqueReference = tokenUniqueReference;
        this.action = action;
        this.timestamp = timestamp;
        this.status = status;
    }
}
