package com.nd2k.follow_up.baby.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddParentRequest(@NotBlank @Email String email) {}
