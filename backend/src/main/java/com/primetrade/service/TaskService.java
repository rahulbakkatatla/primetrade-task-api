package com.primetrade.service;

import com.primetrade.dto.Dto.*;
import com.primetrade.entity.Task;
import com.primetrade.entity.User;
import com.primetrade.repository.TaskRepository;
import com.primetrade.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository; this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public TaskResponse create(TaskRequest request) {
        User owner = getCurrentUser();
        Task task = Task.builder().title(request.getTitle()).description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Task.Status.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : Task.Priority.MEDIUM)
                .owner(owner).build();
        return TaskResponse.from(taskRepository.save(task));
    }

    public List<TaskResponse> getMyTasks() {
        return taskRepository.findByOwner(getCurrentUser()).stream().map(TaskResponse::from).collect(Collectors.toList());
    }

    public TaskResponse getById(Long id) {
        User user = getCurrentUser();
        boolean isAdmin = user.getRole() == User.Role.ROLE_ADMIN;
        Task task = isAdmin ? taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"))
                : taskRepository.findByIdAndOwner(id, user).orElseThrow(() -> new RuntimeException("Task not found"));
        return TaskResponse.from(task);
    }

    public TaskResponse update(Long id, TaskRequest request) {
        User user = getCurrentUser();
        Task task = taskRepository.findByIdAndOwner(id, user)
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        return TaskResponse.from(taskRepository.save(task));
    }

    public void delete(Long id) {
        User user = getCurrentUser();
        boolean isAdmin = user.getRole() == User.Role.ROLE_ADMIN;
        Task task = isAdmin ? taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"))
                : taskRepository.findByIdAndOwner(id, user).orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream().map(TaskResponse::from).collect(Collectors.toList());
    }
}
