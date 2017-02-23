package com.comviva.mfs.promotion.modules.pack.repository;

import com.comviva.mfs.promotion.builder.PackPolicyBuilder;
import com.comviva.mfs.promotion.modules.pack.domain.PackPolicy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sumit.das on 12/28/2016.
 */
public class PackPolicyCustomRepositoryImplMockedTest {
    @InjectMocks
    private PackPolicyCustomRepository packPolicyCustomRepository = new PackPolicyCustomRepositoryImpl();
    @Mock
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        Query mockedQuery = mock(Query.class);
        Query mockedQuery2 = mock(Query.class);

        when(mockedQuery.setParameter("type", "foo")).thenReturn(mockedQuery2);
        when(mockedQuery2.getResultList()).thenReturn(Collections.emptyList());
        when(entityManager
                .createQuery("Select policy FROM PackPolicy policy where policy.type = :type")).thenReturn(mockedQuery);
    }

    @Test
    public void updateAndSaveShouldPersistWhenPackIdIsEmpty()  {
        PackPolicy packPolicy = new PackPolicyBuilder().setId(null).setType("foo").build();

        packPolicyCustomRepository.updateOrSave(packPolicy);

        verify(entityManager).persist(packPolicy);
    }

    @Test
    public void updateAndSaveShouldMergeWhenPackIdIsNotEmpty()  {
        PackPolicy packPolicy = new PackPolicyBuilder().setId("123").setType("foo").build();

        packPolicyCustomRepository.updateOrSave(packPolicy);

        verify(entityManager).merge(packPolicy);
    }


    @Test
    public void updateAndSaveShouldFlushExistingDataPresentInTheSession()  {
        PackPolicy packPolicy = new PackPolicyBuilder().setId("123").setType("foo").build();

        packPolicyCustomRepository.updateOrSave(packPolicy);

        verify(entityManager).flush();
    }
}