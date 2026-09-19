package com.nd2k.follow_up.user.out.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    @SuppressWarnings("unused")
    protected UserEntity() {}

    public UserEntity(Long id, String email, String passwordHash, String name) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
}
