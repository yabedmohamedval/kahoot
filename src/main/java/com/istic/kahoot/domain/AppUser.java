package com.istic.kahoot.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_app_user_username", columnNames = "username"),
                @UniqueConstraint(name="uk_app_user_email",    columnNames = "email")
        })
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)  private String username;
    @Column(nullable = false)  private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // TEACHER / PLAYER

    @Column(nullable = false, length = 80)
    private String passwordHash;  // <-- BCrypt (ex: $2a$10$...)

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
