package com.comviva.mfs.hce.appserver.mapper.pojo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogsRequest {
    @NotNull
    private Date fromDate;
    @NotNull
    private Date toDate;
}
