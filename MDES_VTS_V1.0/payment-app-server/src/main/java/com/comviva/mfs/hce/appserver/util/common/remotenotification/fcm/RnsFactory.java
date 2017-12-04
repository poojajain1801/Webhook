package com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm;

import org.springframework.core.env.Environment;

/**
 * Factory class for RemoteNotification.
 * Created by tarkeshwar.v on 2/14/2017.
 */
public class RnsFactory {
    public static enum RNS_TYPE {
        GCM, FCM
    }

    private static RemoteNotification remoteNotification;

    /**
     * Returns implementation of RemoteNotification.
     * @param rnsType   Type of RNS implementation
     * @return Response
     */
    public static RemoteNotification getRnsInstance(RNS_TYPE rnsType, Environment env) {
        if(remoteNotification == null) {
            switch (rnsType) {
                case FCM:
                    remoteNotification = new FcmRns(env);
                    break;
            }
        }
        return remoteNotification;
    }
}
