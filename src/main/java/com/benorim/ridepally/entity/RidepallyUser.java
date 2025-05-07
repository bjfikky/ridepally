package com.benorim.ridepally.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RidepallyUser {
    @Id
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private boolean accountNonLocked;

    @Column(nullable = false)
    private boolean accountNonExpired;

    @Column(nullable = false)
    private boolean credentialsNonExpired;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ridepally_user_roles",
            joinColumns = @JoinColumn(name = "ridepally_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    @ToString.Exclude
//    private List<Vehicle> vehicles = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
