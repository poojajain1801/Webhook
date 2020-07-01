package com.comviva.mfs.hce.appserver.service.contract;
import com.comviva.mfs.hce.appserver.mapper.pojo.AuditLogsRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConsumerReportReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.DeviceReportReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.UserDeviceCardReportReq;

import java.util.Map;

/**
 * Created by rishikesh.kumar on 09-01-2019.
 */
public interface ReportsService {

    Map<String, Object> consumerReport(ConsumerReportReq consumerReportReq);
    Map<String,Object> deviceReport(DeviceReportReq deviceReportReqPojo);
    Map<String,Object> userDeviceCardReport(UserDeviceCardReportReq userDeviceCardReportReqPojo);
    Map<String, Object> auditLogs(AuditLogsRequest auditLogsRequest);
}
