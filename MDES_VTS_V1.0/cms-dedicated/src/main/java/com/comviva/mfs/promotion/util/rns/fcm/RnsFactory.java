package com.comviva.mfs.promotion.util.rns.fcm;

/**
 * Factory class for RemoteNotificationService.
 * Created by tarkeshwar.v on 2/14/2017.
 */
public class RnsFactory {
    public static enum RNS_TYPE {
        GCM, FCM
    }

    private static RemoteNotificationService remoteNotificationService;

    /**
     * Returns implementation of RemoteNotificationService.
     * @param rnsType   Type of RNS implementation
     * @return Response
     */
    public static RemoteNotificationService getRnsInstance(RNS_TYPE rnsType) {
        if(remoteNotificationService == null) {
            switch (rnsType) {
                case FCM:
                    remoteNotificationService = new FcmRns();
                    break;
            }
        }
        return remoteNotificationService;
    }
}
