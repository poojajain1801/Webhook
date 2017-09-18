package com.comviva.mfs.hce.appserver.authentication;

import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.apache.commons.collections4.MapUtils.getObject;
import static org.apache.commons.collections4.MapUtils.getString;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

@Getter
@ToString
@EqualsAndHashCode
public class User implements UserDetails {
    public static final String EMPTY_STRING = "";
    public static final String ANONYMOUS_USER_USERNAME = "anonymous.user";
    public static final String ANONYMOUS_USER_NAME = "Anonymous User";
    private final String id;
    private final String username;
    private final String displayName;
    private final List<String> permissions;
    private final String mobileNumber;
    private final String emailAddress;
    private static final List<String> USER_PERMISSIONS = Arrays.asList("SUBMIT", "EDIT", "VIEW");

    public User(String id, String username, String displayName, String mobileNumber, String emailAddress, List<String> permissions) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        this.mobileNumber = mobileNumber;
        this.permissions = firstNonNull(permissions, new ArrayList<String>());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(permissions.toArray(new String[permissions.size()]));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return EMPTY_STRING;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static User anonymousUser() {
        return new User(ANONYMOUS_USER_USERNAME, ANONYMOUS_USER_USERNAME, ANONYMOUS_USER_NAME, null, null, USER_PERMISSIONS);
    }

    public static User systemUser(String name, List<String> permissions) {
        return new User(name, name, name, null, null, permissions);
    }

    public Map toClaimDetails() {
        Map map = this.toMap();
        return map;
    }

    public Map toMap() {
        Map map = new HashMap<>();
        map.put("registrationId", id);
        map.put("username", username);
        map.put("name", displayName);
        map.put("mobileNumber", mobileNumber);
        map.put("permissions", permissions);
        return map;
    }

    public String toJson() {
        return JsonUtil.toJson(this.toMap());
    }

    public static User fromMap(Map map) {
        String id = getString(map, "registrationId");
        String username = getString(map, "username");
        String name = getString(map, "name");
        String mobileNumber = getString(map, "mobileNumber");
        String emailAddress = getString(map, "emailAddress");
        List<String> permissions = (List<String>) getObject(map, "permissions");
        return new User(id, username, name, mobileNumber, emailAddress, permissions);
    }
}
