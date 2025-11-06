package com.istic.kahoot.security;


import com.istic.kahoot.domain.AppUser;
import com.istic.kahoot.repository.AppUserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final AppUserRepository repo;
    public JpaUserDetailsService(AppUserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        AppUser u = repo.findByUsername(usernameOrEmail)
                .or(() -> repo.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));

        // Spring attend "ROLE_..."
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()));
        return new User(u.getUsername(), u.getPasswordHash(), authorities);
    }
}
