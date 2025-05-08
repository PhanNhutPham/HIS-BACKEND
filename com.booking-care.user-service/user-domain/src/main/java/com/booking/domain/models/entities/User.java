package com.booking.domain.models.entities;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails{
    @Id
    @Column(name = "user_id")
    private String userId;

    @PrePersist
    public void generateId() {
        this.userId = "user_id" + UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "first_name", length = 35)
    private String firstName;

    @Column(name = "last_name", length = 35)
    private String lastName;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "address", length = 250)
    private String address;

    @Column(name = "profile_image", length = 250)
    private String profileImage;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "is_active")
    private boolean active;


    @JsonIgnore
    private String otp;

    @JsonIgnore
    private LocalDateTime expiryOtp;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    private LocalDateTime create_at;

    private LocalDateTime update_at;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Nếu roles null, trả về danh sách rỗng
        return roles == null ? Collections.emptyList() :
                roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.getNameRole()))
                        .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        // Nếu username tồn tại trả về username, nếu không thì trả về email
        if (username != null && !username.isEmpty()) {
            return username;
        } else if (email != null && !email.isEmpty()) {
            return email;
        }
        return "";
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
        return active;
    }
}
