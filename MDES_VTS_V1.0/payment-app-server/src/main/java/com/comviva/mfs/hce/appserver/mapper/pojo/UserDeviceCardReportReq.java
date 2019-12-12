package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by rishikesh.kumar on 29-01-2019.
 */
@Getter
@Setter
public class UserDeviceCardReportReq {
    private Date fromDate;
    private Date toDate;
    private String userId;
    private String deviceId;
    private String userStatus;
    private String deviceStatus;

    public UserDeviceCardReportReq(Date fromDate, Date toDate, String userId, String deviceId, String userStatus, String deviceStatus) {
        this.fromDate = (fromDate);
        this.toDate = (toDate);
        this.userId = userId;
        this.deviceId = deviceId;
        this.userStatus = userStatus;
        this.deviceStatus = deviceStatus;
    }

    public UserDeviceCardReportReq() {

    }
}
