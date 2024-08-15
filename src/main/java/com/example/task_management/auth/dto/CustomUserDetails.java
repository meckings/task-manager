package com.example.task_management.auth.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class CustomUserDetails implements Serializable {

    private String username;
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private Set<String> authorities;

    public CustomUserDetails(UserDetails userDetails) {
        this.username = userDetails.getUsername();
        this.password = userDetails.getPassword();
        this.accountNonExpired = userDetails.isAccountNonExpired();
        this.accountNonLocked = userDetails.isAccountNonLocked();
        this.credentialsNonExpired = userDetails.isCredentialsNonExpired();
        this.enabled = userDetails.isEnabled();
        this.authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public void eraseCredentials() {
        this.password = null;
    }
}
