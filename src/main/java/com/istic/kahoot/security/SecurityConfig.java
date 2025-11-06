package com.istic.kahoot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Configuration
@EnableWebSecurity          // <— important
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    SecurityFilterChain filter(HttpSecurity http,
                               ClientRegistrationRepository clients) throws Exception {

        var oidcUserService = new OidcUserService() {
            @Override public OidcUser loadUser(OidcUserRequest req) {
                OidcUser u = super.loadUser(req);
                Map<String,Object> realm = u.getClaimAsMap("realm_access");
                Collection<String> roles = (realm != null && realm.get("roles") instanceof Collection<?> c)
                        ? c.stream().map(Object::toString).toList()
                        : List.of();
                Set<GrantedAuthority> auths = new HashSet<>(u.getAuthorities());
                roles.forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
                return new DefaultOidcUser(auths, u.getIdToken(), u.getUserInfo(), "preferred_username");
            }
        };

        var oidcLogout = new OidcClientInitiatedLogoutSuccessHandler(clients);
        oidcLogout.setPostLogoutRedirectUri("{baseUrl}/quizzes");

        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2/**"))
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/h2/**").permitAll()
                        .requestMatchers("/quizzes","/quizzes/**").hasAnyRole("PLAYER","TEACHER")
                        .requestMatchers("/api/quizzes/**").hasAnyRole("PLAYER","TEACHER")
                        .requestMatchers("/api/admin/**").hasRole("TEACHER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(o -> o
                        .userInfoEndpoint(ui -> ui.oidcUserService(oidcUserService))
                        .defaultSuccessUrl("/quizzes", true))
                .logout(l -> l.logoutSuccessHandler(oidcLogout))
                .build();
    }

}
