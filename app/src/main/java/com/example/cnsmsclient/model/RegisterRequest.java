package com.example.cnsmsclient.model;

public class RegisterRequest {
    private final String name;
    private final String email;
    private final String password;
    private final String role;

    public RegisterRequest(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static class EmailOnly {
        private final String email;
        public EmailOnly(String email) {
            this.email = email;
        }
    }
}
