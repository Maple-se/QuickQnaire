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
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    // 注册接口：用户创建
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User newUser) {
        try {
            // 调用服务层创建用户
            userService.createUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("用户注册成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());  // 返回错误消息
        }
    }

    // 登录接口：用户登录
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 使用 AuthenticationManager 进行身份验证
            Authentication authentication = authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());

            User user = userService.getUserByUsername(loginRequest.getUsername());

            // 生成 JWT Token
            String token = JwtTokenUtil.generateToken(user);

            // 返回包含 token 和用户角色的响应
            return ResponseEntity.ok(new LoginResponse(token, "login successfully"));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // 用户名和密码验证
    private Authentication authenticateUser(String username, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

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

