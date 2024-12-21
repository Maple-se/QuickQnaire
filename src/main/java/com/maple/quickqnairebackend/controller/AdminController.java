package com.maple.quickqnairebackend.controller;

import com.maple.quickqnairebackend.dto.UserDTO;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by zong chang on 2024/11/30 20:29
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/quickqnaire")
public class AdminController {

    private final UserService userService;

    // 根据用户名查找用户
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find-user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(userService.userToDTO(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");  // 用户不存在
        }
    }
    // 删除用户
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 删除成功，返回204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());  // 用户不存在
        }
    }

    // 获取所有用户
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(userService.usersToDTO(users));
    }
}

