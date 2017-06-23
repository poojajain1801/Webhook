package com.comviva.mfs.promotion.modules.mpamanagement.domain;

import com.comviva.mfs.promotion.modules.mpamanagement.model.RnsInfo;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QRnsInfo is a Querydsl query type for RnsInfo
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QRnsInfo extends EntityPathBase<RnsInfo> {

    private static final long serialVersionUID = -1325687233L;

    public static final QRnsInfo rnsInfo = new QRnsInfo("rnsInfo");

    public final StringPath id = createString("id");

    public final StringPath paymentAppInstId = createString("paymentAppInstId");

    public final StringPath rnsRegistrationId = createString("rnsRegistrationId");

    public final StringPath rnsType = createString("rnsType");

    public QRnsInfo(String variable) {
        super(RnsInfo.class, forVariable(variable));
    }

    public QRnsInfo(Path<? extends RnsInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRnsInfo(PathMetadata metadata) {
        super(RnsInfo.class, metadata);
    }

}

