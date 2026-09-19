package com.nd2k.follow_up.user.core.domain;

public class User {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final String name;

    private User(Long id, String email, String passwordHash, String name) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
    }

    public static User create(String email, String passwordHash, String name) {
        return new User(null, email, passwordHash, name);
    }

    public static User reconstitute(Long id, String email, String passwordHash, String name) {
        return new User(id, email, passwordHash, name);
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
}
