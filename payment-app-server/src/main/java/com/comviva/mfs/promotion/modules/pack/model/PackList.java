package com.comviva.mfs.promotion.modules.pack.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by sumit.das on 12/26/2016.
 */
public class PackList extends ArrayList<Pack> {
    public PackList(Collection<Pack> packs) {
        super(packs);
    }

    public static PackList fromMapList(List<Map> packList) {
        if (packList == null) {
            return null;
        }
       return new PackList(mapList(packList, Pack::fromMap));
    }

    public List<Map> toMapList() {
        return mapList(this, Pack::toMap);
    }

    public static <T, R> List<R> mapList(List<T> sourceList, Function<T, R> mapFunction) {
        if(sourceList == null) {
            return null;
        }
        return sourceList.stream().map(mapFunction).collect(Collectors.toList());
    }
}
