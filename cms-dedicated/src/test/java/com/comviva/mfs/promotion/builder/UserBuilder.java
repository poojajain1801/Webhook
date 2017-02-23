package com.comviva.mfs.promotion.builder;

import com.comviva.mfs.promotion.authentication.User;


import java.util.Arrays;
import java.util.List;

/**
 * Created by sumit.das on 12/22/2016.
 */
public class UserBuilder {
    public static  String ANONYMOUS_USER_USERNAME = "anonymous.user";
    public static  String ANONYMOUS_USER_NAME = "Anonymous User";
    private String id;
    private String username;
    private String displayName;
    private List<String> permissions;
    private String mobileNumber;
    private String emailAddress;
    private static  List<String> USER_PERMISSIONS = Arrays.asList("SUBMIT", "EDIT", "VIEW");

    public UserBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public UserBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserBuilder setPermissions(List<String> permissions) {
        this.permissions = permissions;
        return this;
    }

    public UserBuilder setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        return this;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public UserBuilder setPermissions(String... permissions) {
        return setPermissions(Arrays.asList(permissions));
    }

    public User build() {
        return new User(id, username, displayName,mobileNumber,emailAddress,permissions);
    }

    public UserBuilder withStubData() {
        setId("PT.123.567.890");
        setUsername("jane.doe");
        setDisplayName("Jane Doe");
        setMobileNumber("7777777");
        setEmailAddress("jan.doe");
        setPermissions("BUY","SUBSCRIBE");
        return this;
    }
    public UserBuilder anonymousUser() {
        setId(ANONYMOUS_USER_USERNAME);
        setUsername(ANONYMOUS_USER_USERNAME);
        setDisplayName(ANONYMOUS_USER_NAME);
        setMobileNumber(null);
        setEmailAddress(null);
        setPermissions(USER_PERMISSIONS);
        return this;
    }


}
