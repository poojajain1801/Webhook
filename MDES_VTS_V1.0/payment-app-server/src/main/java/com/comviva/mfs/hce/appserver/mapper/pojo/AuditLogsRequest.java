package com.comviva.mfs.hce.appserver.mapper.pojo;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class AuditLogsRequest {
    @NotNull
    private Date fromDate;
    @NotNull
    private Date toDate;

    public AuditLogsRequest(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public AuditLogsRequest() {
    }
}
