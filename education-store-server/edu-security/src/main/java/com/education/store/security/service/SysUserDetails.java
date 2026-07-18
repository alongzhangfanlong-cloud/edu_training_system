package com.education.store.security.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security 用户详情
 */
@Getter
@Setter
public class SysUserDetails implements UserDetails {

    private Long userId;
    private String username;
    private String password;
    private String realName;
    private Integer status;
    private Set<String> permissions;
    private List<SimpleGrantedAuthority> authorities;

    public SysUserDetails(Long userId, String username, String password, String realName,
                          Integer status,
                          Set<String> permissions, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.status = status;
        this.permissions = permissions;
        this.authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
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
        return status != null && status == 1;
    }
}
