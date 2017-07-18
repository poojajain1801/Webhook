package com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm;

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
    public static RemoteNotification getRnsInstance(RNS_TYPE rnsType) {
        if(remoteNotification == null) {
            switch (rnsType) {
                case FCM:
                    remoteNotification = new FcmRns();
                    break;
            }
        }
        return remoteNotification;
    }
}
