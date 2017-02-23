package com.comviva.mfs.promotion.modules.pack.repository;

import com.comviva.mfs.promotion.builder.PackPolicyBuilder;
import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;
import com.comviva.mfs.promotion.modules.pack.model.Pack;
import com.comviva.mfs.promotion.modules.pack.model.PackStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by sumit.das on 12/26/2016.
 */

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class PackPolicyRepositoryTest {

    @Resource
    private PackPolicyRepository packPolicyRepository;

    @Test
    public void findByTypeShouldReturnPackPolicyBasedOnType() throws Exception {
        PackPolicy newPack =  new PackPolicyBuilder().setId(null).setType("default").setPacks(Arrays.asList(Pack.builder().packId("1").packName("Promo Pack").status(PackStatus.ACTIVE).startDate(null).build())).build();
        packPolicyRepository.save(newPack);

        PackPolicy retrievedPack = packPolicyRepository.findByType("default").get();

        assertThat(retrievedPack.getId(), is(notNullValue()));
        assertThat(retrievedPack.getType(), is("default"));
        assertThat(retrievedPack.getPacks().get(0).getPackId(), is("1"));
        assertThat(retrievedPack.getPacks().get(0).getPackName(), is("Promo Pack"));
        assertThat(retrievedPack.getPacks().get(0).getStatus(), is(PackStatus.ACTIVE));
    }

    @Test
    public void findByTypeShouldNullWhenNoPackIsFoundOfASpecificType() throws Exception {
        PackPolicy newPack = new PackPolicyBuilder().setId(null).setType("default").setPacks(Arrays.asList(Pack.builder().packId("1").packName("Promo Pack").status(PackStatus.ACTIVE).startDate(null).build())).build();
        packPolicyRepository.save(newPack);

        Optional<PackPolicy> retrievedPack = packPolicyRepository.findByType("foo");

        assertThat(retrievedPack.isPresent(), is(false));
    }
}