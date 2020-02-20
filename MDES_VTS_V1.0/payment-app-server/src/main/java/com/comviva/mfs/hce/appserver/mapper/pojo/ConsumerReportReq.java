package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;
import oracle.sql.TIMESTAMP;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by rishikesh.kumar on 09-01-2019.
 */
@Getter
@Setter
public class ConsumerReportReq {
    private Date fromDate;
    private Date toDate;
    private String userId;
    private String status;

    public ConsumerReportReq(Date fromDate, Date toDate, String userId, String status) {
        this.fromDate = (fromDate);
        this.toDate = (toDate);
        this.userId = userId;
        this.status = status;
    }

    public ConsumerReportReq() {

    }
}
