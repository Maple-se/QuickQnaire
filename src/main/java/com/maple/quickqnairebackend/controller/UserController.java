package com.maple.quickqnairebackend.controller;

import com.maple.quickqnairebackend.dto.LoginRequest;
import com.maple.quickqnairebackend.dto.LoginResponse;
import com.maple.quickqnairebackend.dto.UserDTO;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.service.UserService;
import com.maple.quickqnairebackend.util.JwtTokenUtil;
import com.maple.quickqnairebackend.validation.UserCreateGroup;
import com.maple.quickqnairebackend.validation.UserUpdateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

/**
 * Created by zong chang on 2024/12/1 16:31
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/quickqnaire")
public class UserController {


    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    // 注册接口：用户创建
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Validated(UserCreateGroup.class) @RequestBody UserDTO newUser) {
        try {
            // 调用服务层创建用户
            userService.createUser(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("User Register Success");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());  // 返回错误消息
        }
    }

    // 登录接口：用户登录
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 使用 AuthenticationManager 进行身份验证
            Authentication authentication = authenticateUser(loginRequest);

            // 返回包含 token 和用户角色的响应
            return ResponseEntity.ok(new LoginResponse(jwtTokenUtil.generateToken(authentication), "login successfully"));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // 更新用户信息：更新用户名、邮箱
    @PutMapping("/update-user-info")
    public ResponseEntity<?> updateUser(@Validated(UserUpdateGroup.class) @RequestBody UserDTO updatedUser) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 userId
            if(!Objects.equals(updatedUser.getId(), userId)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User Identity Error");
            }
            User preUser = userService.getUserById(userId);
            UserDTO updated = userService.updateUser(preUser, updatedUser);
            return ResponseEntity.ok(updated);  // 返回更新后的用户
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());  // 错误处理
        }
    }


    // ToDo:更新用户密码


    //ToDo:登出接口：待考虑

    // 用户名和密码验证
    private Authentication authenticateUser(LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
    }

}
