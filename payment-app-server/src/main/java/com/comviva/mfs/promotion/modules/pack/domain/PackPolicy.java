package com.comviva.mfs.promotion.modules.pack.domain;

import com.comviva.mfs.promotion.model.error.PropertyErrors;
import com.comviva.mfs.promotion.model.validation.ValidationContext;
import com.comviva.mfs.promotion.model.validation.ValidationDslBuilder;
import com.comviva.mfs.promotion.modules.pack.domain.converter.PackDetailListJsonConverter;
import com.comviva.mfs.promotion.modules.pack.model.Pack;
import com.comviva.mfs.promotion.modules.pack.model.PackConfiguration;
import com.comviva.mfs.promotion.modules.pack.validation.PackValidator;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * Created by sumit.das on 12/25/2016.
 */
//@Builder(toBuilder = true)
@Entity
@Getter
@Table(name = "PACK_POLICY")
@ToString
//@AllArgsConstructor
public class PackPolicy implements PackValidator {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private final String id;

    @Column(name = "TYPE")
    private final String type;

    @Column(name = "PACKS")
    @Convert(converter = PackDetailListJsonConverter.class)
    private final List<Pack> packs;

    public PackPolicy() {
        this(null,null,null);
    }

    public PackPolicy(String id, String type, List<Pack> packs) {
        this.id = id;
        this.type = type;
        this.packs = packs;
    }

    @Override
    public PropertyErrors validate(ValidationContext validationContext) {
        return new ValidationDslBuilder(validationContext)
                .validateRequired("type")
                .validateNestedObject("packs")
                .getErrors();
    }

    @Override
    public PropertyErrors validate(PackConfiguration packConfiguration) {
        return validate(new ValidationContext(new PropertyErrors(this, "packPolicy"), packConfiguration));
    }
}
