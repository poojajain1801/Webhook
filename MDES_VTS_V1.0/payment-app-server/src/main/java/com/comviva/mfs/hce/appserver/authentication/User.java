/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.comviva.mfs.hce.appserver.authentication;

import com.comviva.mfs.hce.appserver.util.common.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


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
        return this.toMap();
    }

    private Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
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
