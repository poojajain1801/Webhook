package com.comviva.mfs.promotion.modules.mpamanagement.domain;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import javax.annotation.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QDevieceDetail is a Querydsl query type for SessionInfo
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDevieceDetail extends EntityPathBase<ApplicationInstanceInfo> {

    private static final long serialVersionUID = -668071426L;

    public static final QDevieceDetail devieceDetail = new QDevieceDetail("devieceDetail");

    public final StringPath deviceInfo = createString("deviceInfo");

    public final StringPath devieceName = createString("devieceName");

    public final StringPath formFactor = createString("formFactor");

    public final StringPath id = createString("id");

    public final StringPath imei = createString("imei");

    public final StringPath msisdn = createString("msisdn");

    public final StringPath nfcCapable = createString("nfcCapable");

    public final StringPath osName = createString("osName");

    public final StringPath osVersion = createString("osVersion");

    public final StringPath serialNumber = createString("serialNumber");

    public final StringPath storageTechnology = createString("storageTechnology");

    public QDevieceDetail(String variable) {
        super(ApplicationInstanceInfo.class, forVariable(variable));
    }

    public QDevieceDetail(Path<? extends ApplicationInstanceInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDevieceDetail(PathMetadata metadata) {
        super(ApplicationInstanceInfo.class, metadata);
    }

}

