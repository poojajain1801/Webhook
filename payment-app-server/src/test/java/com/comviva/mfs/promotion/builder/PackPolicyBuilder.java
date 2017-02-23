package com.comviva.mfs.promotion.builder;

import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;
import com.comviva.mfs.promotion.modules.pack.model.Pack;

import java.util.Arrays;
import java.util.List;

/**
 * Created by charu.sharma on 12/29/2016.
 */
public class PackPolicyBuilder {
    private String id;
    private String type;
    private List<Pack> packs;

    public PackPolicyBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public PackPolicyBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public PackPolicyBuilder setPacks(List<Pack> packs) {
        this.packs = packs;
        return this;
    }

    public PackPolicy build() {
        return new PackPolicy(id, type, packs);
    }

    public PackPolicyBuilder withStubData() {
        setId("1");
        setType("Promo Pack");
        setPacks(Arrays.asList(
                Pack.builder().build()
        ));
        return this;
    }
}


