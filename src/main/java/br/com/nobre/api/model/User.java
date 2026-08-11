package br.com.nobre.api.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "users")
public class User {
    public enum Role { CUSTOMER, ADMIN }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable=false) public String name;
    @Column(nullable=false, unique=true) public String email;
    @Column(name="password_hash", nullable=false) public String passwordHash;
    public String phone;
    @Enumerated(EnumType.STRING) @Column(nullable=false) public Role role = Role.CUSTOMER;
    @Column(name="created_at", nullable=false) public Instant createdAt = Instant.now();
}
