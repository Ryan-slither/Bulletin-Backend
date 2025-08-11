package com.bulletin.users.model;

import jakarta.persistence.*;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private Long timeCreated;

    @Column(name = "verification_code")
    private Long verificationCode;

    private boolean enabled;

    @Column(name = "password_code")
    private Long passwordCode;

    public Long getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(Long verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Long getPasswordCode() {
        return passwordCode;
    }

    public void setPasswordCode(Long passwordCode) {
        this.passwordCode = passwordCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
