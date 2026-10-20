package com.scc.clinic.model;

public record User(
        int id,
        String fullName,
        String username,
        String password,
        String role,
        String status,
        String createdAt
) {
}
