package com.comviva.mfs.promotion.modules.pack.service;

import com.comviva.mfs.promotion.builder.PackPolicyBuilder;
import com.comviva.mfs.promotion.exception.ValidationException;
import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;
import com.comviva.mfs.promotion.modules.pack.model.Pack;
import com.comviva.mfs.promotion.modules.pack.model.PackStatus;
import com.comviva.mfs.promotion.modules.pack.repository.PackPolicyCustomRepository;
import com.comviva.mfs.promotion.modules.pack.repository.PackPolicyRepository;
import com.comviva.mfs.promotion.modules.pack.service.contract.PackPolicyService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sumit.das on 12/28/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PackPolicyServiceImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private PackPolicyRepository packPolicyRepository;
    @Mock
    private PackPolicyCustomRepository packPolicyCustomRepository;
    private PackPolicyService packPolicyService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        packPolicyService = new PackPolicyServiceImpl(packPolicyRepository, packPolicyCustomRepository);
    }

    @Test
    public void saveOrUpdateShouldThrowErrorWhenPolicyToBeInsertedIsNotProper() throws Exception {
        PackPolicy packPolicy =  new PackPolicyBuilder().setPacks(Arrays.asList(Pack.builder().status(PackStatus.ACTIVE).build())).build();

        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Error during saving policy");

        packPolicyService.saveOrUpdate(packPolicy);
    }

    @Test
    public void saveOrUpdateShouldStorePackPolicy() throws Exception {
        PackPolicy packPolicy =  new PackPolicyBuilder().setType("foo").setPacks(Arrays.asList(Pack.builder().packName("my pack").status(PackStatus.ACTIVE).startDate(DateTime.parse("2016-08-01").toDate()).build())).build();
        when(packPolicyCustomRepository.updateOrSave(packPolicy)).thenReturn(packPolicy);

        PackPolicy policy = packPolicyService.saveOrUpdate(packPolicy);

        assertThat(policy.getType(), is("foo"));
        assertThat(policy.getPacks().size(), is(1));
        assertThat(policy.getPacks().get(0).getPackName(), is("my pack"));
        assertThat(policy.getPacks().get(0).getStatus(), is(PackStatus.ACTIVE));
    }

    @Test
    public void getPackPolicyBasedOnTypeShouldRetrievePackPolicyFromDb() throws Exception {
        PackPolicy packPolicy =  new PackPolicyBuilder().setType("foo").setPacks(Arrays.asList(Pack.builder().packName("my pack").startDate(DateTime.parse("2016-08-01").toDate()).build())).build();
        when(packPolicyRepository.findByType("foo")).thenReturn(Optional.of(packPolicy));

        PackPolicy retrievedPolicy = packPolicyService.getPackPolicyBasedOnType("foo");

        assertThat(retrievedPolicy, is(packPolicy));
    }

    @Test
    public void getPackPolicyBasedOnTypeShouldNullIfParticularTypeOfPackPolicyIsNotPresent() throws Exception {
        when(packPolicyRepository.findByType("foo")).thenReturn(Optional.empty());

        PackPolicy retrievedPolicy = packPolicyService.getPackPolicyBasedOnType("foo");

        assertThat(retrievedPolicy, is(nullValue()));
    }

}