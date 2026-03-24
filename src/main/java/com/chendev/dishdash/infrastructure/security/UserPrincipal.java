package com.chendev.dishdash.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;


    public static UserPrincipal of(Long id, String username,
                                   String password, String role) {
        GrantedAuthority authority = () -> role;
        return new UserPrincipal(id, username, password, List.of(authority));
    }

    // UserDetails boilerplate.
    // Account locking and expiry are handled at the application layer, not via these flags.
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
