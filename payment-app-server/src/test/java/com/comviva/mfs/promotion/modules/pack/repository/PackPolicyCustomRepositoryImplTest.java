package com.comviva.mfs.promotion.modules.pack.repository;

import com.comviva.mfs.promotion.builder.PackPolicyBuilder;
import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;
import com.comviva.mfs.promotion.modules.pack.model.Pack;
import com.comviva.mfs.promotion.modules.pack.model.PackStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by sumit.das on 12/27/2016.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class PackPolicyCustomRepositoryImplTest {
    @Autowired
    private PackPolicyCustomRepository packPolicyCustomRepository;

    @Test
    public void updateOrModifyShouldStorePolicyInTheDatabase() throws Exception {
        PackPolicy newPack = new PackPolicyBuilder().setId(null).setType("test").setPacks(Arrays.asList(Pack.builder().packId("1").packName("Promo Pack2").status(PackStatus.ACTIVE).startDate(null).build())).build();
        PackPolicy packPolicy = packPolicyCustomRepository.updateOrSave(newPack);

        assertThat(packPolicy.getId(), is(notNullValue()));
        assertThat(packPolicy.getPacks().get(0).getPackName(), is("Promo Pack2"));
    }
}