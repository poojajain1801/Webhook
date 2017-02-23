package com.comviva.mfs.promotion.modules.pack.model;

import com.comviva.mfs.promotion.model.error.PropertyErrors;
import com.comviva.mfs.promotion.model.validation.ValidationContext;
import com.comviva.mfs.promotion.model.validation.ValidationDslBuilder;
import com.comviva.mfs.promotion.modules.pack.validation.PackValidator;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.comviva.mfs.promotion.modules.pack.model.PackStatus.getPackStatus;
import static com.comviva.mfs.promotion.util.CastUtil.dateToString;
import static com.comviva.mfs.promotion.util.CastUtil.getDate;
import static org.apache.commons.collections4.MapUtils.getString;

/**
 * Created by sumit.das on 12/25/2016.
 */
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Pack implements PackValidator{
    private final String packId;
    private final String packName;
    private final PackStatus status;
    private final Date startDate;
    private final Date endDate;

    public Pack(String packId, String packName, PackStatus status, Date startDate, Date endDate) {
        this.packId = packId;
        this.packName = packName;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Pack() {
        this(null, null, null, null, null);
    }

    public Map toMap() {
        Map<String, Object> packDetailMap = new HashMap<>();
        packDetailMap.put("packId", this.packId);
        packDetailMap.put("packName", this.packName);
        packDetailMap.put("status", this.status.getStatus());
        packDetailMap.put("startDate", dateToString(this.startDate));
        packDetailMap.put("endDate", dateToString(this.endDate));
        return packDetailMap;
    }

    public static Pack fromMap(Map map) {
        String packId = getString(map, "packId");
        String packName = getString(map, "packName");
        PackStatus status = getPackStatus(getString(map, "status"));
        Date startDate = getDate(map, "startDate");
        Date endDate = getDate(map, "endDate");
        return new Pack(packId, packName, status, startDate, endDate);
    }

    @Override
    public PropertyErrors validate(ValidationContext validationContext) {
        return new ValidationDslBuilder(validationContext)
                .validateRequired("packName", "status", "startDate")
                .getErrors();
    }

    @Override
    public PropertyErrors validate(PackConfiguration packConfiguration) {
        return validate(new ValidationContext(new PropertyErrors(this, "pack"), packConfiguration));
    }
}
