package com.comviva.mfs.promotion.modules.user_management.domain.converter;

import com.comviva.mfs.promotion.modules.user_management.model.CardDetail;
import com.comviva.mfs.promotion.modules.user_management.model.ListOfMap;
import com.comviva.mfs.promotion.util.JsonUtil;

import javax.persistence.AttributeConverter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sumit.das on 12/26/2016.
 */
public class CardDetailListJsonConverter implements AttributeConverter<List<CardDetail>, String>{

    @Override
    public String convertToDatabaseColumn(List<CardDetail> cardDetails) {
        return null;
    }

    @Override
    public List<CardDetail> convertToEntityAttribute(String s) {
        return null;
    }
}
