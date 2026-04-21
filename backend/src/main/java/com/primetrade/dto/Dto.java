package com.primetrade.dto;

import com.primetrade.entity.Task;
import com.primetrade.entity.User;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class Dto {

    // ── Auth ──────────────────────────────────────────────
    public static class RegisterRequest {
        @NotBlank(message = "Username is required") @Size(min = 3, max = 30)
        private String username;
        @NotBlank @Email(message = "Valid email required")
        private String email;
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public void setUsername(String u) { this.username = u; }
        public void setEmail(String e) { this.email = e; }
        public void setPassword(String p) { this.password = p; }
    }

    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public void setUsername(String u) { this.username = u; }
        public void setPassword(String p) { this.password = p; }
    }

    public static class AuthResponse {
        private String token, username, role, message;
        public AuthResponse(String token, String username, String role, String message) {
            this.token=token; this.username=username; this.role=role; this.message=message;
        }
        public String getToken() { return token; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public String getMessage() { return message; }
        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String token, username, role, message;
            public Builder token(String t) { this.token=t; return this; }
            public Builder username(String u) { this.username=u; return this; }
            public Builder role(String r) { this.role=r; return this; }
            public Builder message(String m) { this.message=m; return this; }
            public AuthResponse build() { return new AuthResponse(token, username, role, message); }
        }
    }

    // ── Task ──────────────────────────────────────────────
    public static class TaskRequest {
        @NotBlank(message = "Title is required") @Size(max = 100)
        private String title;
        @Size(max = 1000) private String description;
        private Task.Status status;
        private Task.Priority priority;
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Task.Status getStatus() { return status; }
        public Task.Priority getPriority() { return priority; }
        public void setTitle(String t) { this.title = t; }
        public void setDescription(String d) { this.description = d; }
        public void setStatus(Task.Status s) { this.status = s; }
        public void setPriority(Task.Priority p) { this.priority = p; }
    }

    public static class TaskResponse {
        private Long id; private String title, description, ownerUsername;
        private Task.Status status; private Task.Priority priority;
        private LocalDateTime createdAt, updatedAt;
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Task.Status getStatus() { return status; }
        public Task.Priority getPriority() { return priority; }
        public String getOwnerUsername() { return ownerUsername; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public static TaskResponse from(Task task) {
            TaskResponse r = new TaskResponse();
            r.id=task.getId(); r.title=task.getTitle(); r.description=task.getDescription();
            r.status=task.getStatus(); r.priority=task.getPriority();
            r.ownerUsername=task.getOwner().getUsername();
            r.createdAt=task.getCreatedAt(); r.updatedAt=task.getUpdatedAt();
            return r;
        }
    }

    // ── User ──────────────────────────────────────────────
    public static class UserResponse {
        private Long id; private String username, email; private User.Role role; private LocalDateTime createdAt;
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public User.Role getRole() { return role; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public static UserResponse from(User user) {
            UserResponse r = new UserResponse();
            r.id=user.getId(); r.username=user.getUsername(); r.email=user.getEmail();
            r.role=user.getRole(); r.createdAt=user.getCreatedAt(); return r;
        }
    }

    // ── API wrapper ───────────────────────────────────────
    public static class ApiResponse<T> {
        private boolean success; private String message; private T data;
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public T getData() { return data; }
        public static <T> ApiResponse<T> ok(String message, T data) {
            ApiResponse<T> r = new ApiResponse<>(); r.success=true; r.message=message; r.data=data; return r;
        }
        public static <T> ApiResponse<T> error(String message) {
            ApiResponse<T> r = new ApiResponse<>(); r.success=false; r.message=message; return r;
        }
        public static <T> Builder<T> builder() { return new Builder<>(); }
        public static class Builder<T> {
            private boolean success; private String message; private T data;
            public Builder<T> success(boolean s) { this.success=s; return this; }
            public Builder<T> message(String m) { this.message=m; return this; }
            public Builder<T> data(T d) { this.data=d; return this; }
            public ApiResponse<T> build() {
                ApiResponse<T> r = new ApiResponse<>(); r.success=success; r.message=message; r.data=data; return r;
            }
        }
    }
}
