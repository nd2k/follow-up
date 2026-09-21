package com.nd2k.follow_up.baby.in.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateBabyRequest(@NotBlank String name, LocalDate birthDate) {}
