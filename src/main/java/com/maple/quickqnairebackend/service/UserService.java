package com.maple.quickqnairebackend.service;

/**
 * Created by zong chang on 2024/11/30 19:17
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.maple.quickqnairebackend.dto.UserDTO;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 创建用户
    @Transactional  // 添加事务注解，保证操作的原子性
    public void createUser(UserDTO userDTO) {

        User user = toEntity(userDTO);
        // 在服务层进行唯一性检查
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在");
        }

        // 密码校验
        // 通过正则表达式来验证密码格式
        if (!isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException("密码必须包含字母和数字，且长度至少为6个字符");
        }

        // 密码加密
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 保存用户
        try{
            userRepository.save(user);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage() +" 用户创建失败");
        }
    }


    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        return user;
    }

    //用户名是否存在
    public boolean IsUserNameExist(String username){
        return userRepository.existsByUsername(username);
    }

    // 根据用户名查找用户
    public User getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // 根据ID查找用户
    public User getUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.orElseThrow(() -> new IllegalArgumentException("User not found"));
    }


    // 更新用户名
    public void updateUsername(User user, String newUsername) {
        if (newUsername.equals(user.getUsername())) {
            throw new IllegalArgumentException("应提供新用户名");  // 新用户名与当前用户名相同，抛出异常提示
        }

        // 检查该新用户名是否已存在
        if (userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 更新用户名
        user.setUsername(newUsername);
    }

    // 更新邮箱
    public void updateEmail(User user, String newEmail) {
        if (newEmail.equals(user.getEmail())) {
            throw new IllegalArgumentException("应提供新邮箱");  // 新邮箱与当前邮箱相同，抛出异常提示
        }

        // 检查该新邮箱是否已存在
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        user.setEmail(newEmail);
    }

    // 更新密码
    //ToDo:密码更新方法有待检查
    public void updatePassword(User user, String newPassword) {
        // 如果提供了新的密码
        if (!newPassword.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(newPassword);  // 密码加密
            user.setPassword(encodedPassword);  // 设置加密后的密码
        }
    }

    // 通过用户ID更新用户信息
    @Transactional  // 添加事务注解，保证操作的原子性
    public User updateUser(Long userId, String newUsername, String newEmail, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = optionalUser.get();

        // 更新字段
        updateUsername(user, newUsername);
        updateEmail(user, newEmail);
        updatePassword(user, newPassword);


        // 保存更新后的用户
        return userRepository.save(user);
    }

    // 删除用户
    @Transactional  // 添加事务注解，保证操作的原子性
    public void deleteUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(userId);
    }

    // 获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 密码格式校验方法
    //正则表达式匹配
    //密码必须包含字母和数字，且长度至少为6个字符
    private boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$");
    }
}

