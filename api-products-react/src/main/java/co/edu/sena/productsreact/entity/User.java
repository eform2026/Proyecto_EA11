package co.edu.sena.productsreact.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "username", length = 120)
    private String username;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "reset_token", length = 120)
    private String resetToken;

    @Column(name = "reset_token_expires_at")
    private LocalDateTime resetTokenExpiresAt;

    @Column(nullable = false, length = 20)
    private String role;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void syncUsername() {
        if (username == null || username.isBlank()) {
            username = name;
        }
        role = normalizeRole(role);
    }

    public String getSpringRole() {
        return normalizeRole(role);
    }

    private String normalizeRole(String value) {
        if (value == null || value.isBlank()) {
            return "ROLE_USER";
        }

        return switch (value.trim().toUpperCase()) {
            case "ADMIN", "ROLE_ADMIN" -> "ROLE_ADMIN";
            default -> "ROLE_USER";
        };
    }
}
