package com.maple.quickqnairebackend.controller;

import com.maple.quickqnairebackend.dto.LoginRequest;
import com.maple.quickqnairebackend.dto.LoginResponse;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.service.UserService;
import com.maple.quickqnairebackend.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by zong chang on 2024/11/30 20:29
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@RestController
@RequestMapping("/quickqnaire")
public class UserController {

    @Autowired
    private UserService userService;

    // 根据用户名查找用户
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 用户不存在
        }
    }

    // 根据用户ID查找用户
//    @GetMapping("/id/{userId}")
//    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
//        try {
//            User user = userService.getUserById(userId);
//            return ResponseEntity.ok(user);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 用户不存在
//        }
//    }

    // 更新用户信息
    @PutMapping("/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId,
                                           @Valid @RequestBody User updatedUser) {
        try {
            // 使用 Service 层的 updateUser 方法来处理更新操作
            User updated = userService.updateUser(userId, updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getPassword());
            return ResponseEntity.ok(updated);  // 返回更新后的用户
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // 错误处理
        }
    }


    // 删除用户
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 删除成功，返回204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 用户不存在
        }
    }

    // 获取所有用户
    @GetMapping("/all")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        Iterable<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}

