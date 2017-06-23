package com.comviva.mfs.promotion.modules.mpamanagement.domain;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QMobileEnvironment is a Querydsl query type for SessionInfo
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QMobileEnvironment extends EntityPathBase<ApplicationInstanceInfo> {

    private static final long serialVersionUID = 984051403L;

    public static final QMobileEnvironment mobileEnvironment = new QMobileEnvironment("mobileEnvironment");

    public final StringPath dataEncryptionKey = createString("dataEncryptionKey");

    public final StringPath deviceFingerprint = createString("deviceFingerprint");

    public final StringPath id = createString("id");

    public final StringPath macKey = createString("macKey");

    public final StringPath mobileKeySetId = createString("mobileKeySetId");

    public final StringPath mobilePin = createString("mobilePin");

    public final StringPath paymentAppId = createString("paymentAppId");

    public final StringPath paymentAppInstId = createString("paymentAppInstId");

    public final StringPath rnsRegistrationId = createString("rnsRegistrationId");

    public final StringPath transportKey = createString("transportKey");

    public QMobileEnvironment(String variable) {
        super(ApplicationInstanceInfo.class, forVariable(variable));
    }

    public QMobileEnvironment(Path<? extends ApplicationInstanceInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMobileEnvironment(PathMetadata metadata) {
        super(ApplicationInstanceInfo.class, metadata);
    }

}

