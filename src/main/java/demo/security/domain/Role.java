package demo.security.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 * Created by cengruilin on 2018/1/30.
 */
public enum Role implements GrantedAuthority {
    ADMIN, USER;

    @Override
    public String getAuthority() {
        return toString();
    }
}
