package com.primetrade.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;

    public User() {}

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum Role { ROLE_USER, ROLE_ADMIN }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Task> getTasks() { return tasks; }
    public void setId(Long id) { this.id = id; }
    public void setUsername(String u) { this.username = u; }
    public void setEmail(String e) { this.email = e; }
    public void setPassword(String p) { this.password = p; }
    public void setRole(Role r) { this.role = r; }
    public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }
    public void setTasks(List<Task> t) { this.tasks = t; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id; private String username, email, password; private Role role;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String u) { this.username = u; return this; }
        public Builder email(String e) { this.email = e; return this; }
        public Builder password(String p) { this.password = p; return this; }
        public Builder role(Role r) { this.role = r; return this; }
        public User build() {
            User u = new User(); u.id=id; u.username=username; u.email=email;
            u.password=password; u.role=role; return u;
        }
    }
}
