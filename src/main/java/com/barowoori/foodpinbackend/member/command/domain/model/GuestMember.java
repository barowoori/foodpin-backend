package com.barowoori.foodpinbackend.member.command.domain.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class GuestMember implements UserDetails {

    private final String sessionId;
    private final String socialLoginId;

    public GuestMember(String sessionId, String socialLoginId) {
        this.sessionId = sessionId;
        this.socialLoginId = socialLoginId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + MemberType.UNREGISTERED.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return "GUEST:" + sessionId;
    }
}
