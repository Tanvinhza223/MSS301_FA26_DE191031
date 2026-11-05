package com.mss301.account.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "StoreAccounts")
public class StoreAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "AccountID")
    private Integer accountId;
    @Column(name = "Username", nullable = false, unique = true, length = 20)
    private String username;
    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;
    @Column(name = "Password", nullable = false, length = 100)
    private String password;
    @Column(name = "Role", nullable = false)
    private Integer role;
    @Column(name = "IsActive", nullable = false)
    private Boolean active;

    protected StoreAccount() {}

    public StoreAccount(String username, String email, String password, Integer role, Boolean active) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public Integer getAccountId() { return accountId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Integer getRole() { return role; }
    public Boolean getActive() { return active; }
}

