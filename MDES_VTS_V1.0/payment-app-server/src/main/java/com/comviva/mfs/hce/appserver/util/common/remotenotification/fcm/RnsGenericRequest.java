package com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RnsGenericRequest {
    private String id;
    private UniqueIdType idType;
    private String rnsData;
}
