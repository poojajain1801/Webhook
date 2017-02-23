package com.comviva.mfs.promotion.authentication;

import com.comviva.mfs.promotion.builder.UserBuilder;
import com.comviva.mfs.promotion.util.JsonUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by sumit.das on 12/23/2016.
 */
public class UserTest {

    @Test
    public void isAccountNonExpiredShouldBeTrue() throws Exception {
        assertThat(new UserBuilder().build().isAccountNonExpired(), is(true));
    }

    @Test
    public void isAccountNonLockedShouldBeTrue() throws Exception {
        assertThat(new UserBuilder().build().isAccountNonLocked(), is(true));
    }

    @Test
    public void isCredentialsNonExpiredShouldBeTrue() throws Exception {
        assertThat(new UserBuilder().build().isCredentialsNonExpired(), is(true));
    }

    @Test
    public void isEnabledShouldBeTrue() throws Exception {
        assertThat(new UserBuilder().build().isEnabled(), is(true));
    }

    @Test
    public void fromMapShouldInitializeUserDataFromMap() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "8989-9090");
        map.put("username", "jane.doe");
        map.put("name", "Jane Doe");
        map.put("permissions", Arrays.asList("BUY", "SUBSCRIBE"));

        User user = User.fromMap(map);

        assertThat(user, not(nullValue()));
        assertThat(user.getId(), CoreMatchers.is("8989-9090"));
        assertThat(user.getUsername(), CoreMatchers.is("jane.doe"));
        assertThat(user.getDisplayName(), CoreMatchers.is("Jane Doe"));
        assertThat(user.getAuthorities(), CoreMatchers.is(AuthorityUtils.createAuthorityList("BUY", "SUBSCRIBE")));
    }

    @Test
    public void toJsonShouldHaveJsonStringOfUserToMap() {
        User user = new UserBuilder().withStubData().build();

        String json = user.toJson();

        assertThat(json, is(JsonUtil.toJson(user.toMap())));
    }
}