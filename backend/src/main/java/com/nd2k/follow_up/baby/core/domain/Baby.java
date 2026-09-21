package com.nd2k.follow_up.baby.core.domain;

import java.time.LocalDate;

public class Baby {

    private final Long id;
    private final String name;
    private final LocalDate birthDate;

    private Baby(Long id, String name, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
    }

    public static Baby create(String name, LocalDate birthDate) {
        return new Baby(null, name, birthDate);
    }

    public static Baby reconstitute(Long id, String name, LocalDate birthDate) {
        return new Baby(id, name, birthDate);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getBirthDate() { return birthDate; }
}
