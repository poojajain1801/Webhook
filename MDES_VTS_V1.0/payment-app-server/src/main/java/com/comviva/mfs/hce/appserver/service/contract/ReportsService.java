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
    /**
     * consumerReport
     * @param consumerReportReq consumerReportRequest
     * @return Map
     * */
    Map<String, Object> consumerReport(ConsumerReportReq consumerReportReq);

    /**
     * deviceReport
     * @param deviceReportReqPojo DeviceReportRequest
     * @return Map
     * */
    Map<String,Object> deviceReport(DeviceReportReq deviceReportReqPojo);

    /**
     * userDeviceCardReport
     * @param userDeviceCardReportReqPojo userDeviceCardReportRequest
     * @return Map
     * */
    Map<String,Object> userDeviceCardReport(UserDeviceCardReportReq userDeviceCardReportReqPojo);

    /**
     * auditLogs
     * @param auditLogsRequest auditLogsRequest
     * @return Map
     * */
    Map<String, Object> auditLogs(AuditLogsRequest auditLogsRequest);
}
