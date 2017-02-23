package com.comviva.mfs.promotion.modules.pack.domain.converter;

import com.comviva.mfs.promotion.modules.pack.model.ListOfMap;
import com.comviva.mfs.promotion.modules.pack.model.Pack;
import com.comviva.mfs.promotion.modules.pack.model.PackList;
import com.comviva.mfs.promotion.util.JsonUtil;

import javax.persistence.AttributeConverter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sumit.das on 12/26/2016.
 */
public class PackDetailListJsonConverter implements AttributeConverter<List<Pack>, String>{
    @Override
    public String convertToDatabaseColumn(List<Pack> packs) {
        return JsonUtil.toJson(packs.stream().map(Pack::toMap).collect(Collectors.toList()));
    }

    @Override
    public List<Pack> convertToEntityAttribute(String dbData) {
        return PackList.fromMapList(JsonUtil.fromJson(dbData, ListOfMap.class));
    }
}
