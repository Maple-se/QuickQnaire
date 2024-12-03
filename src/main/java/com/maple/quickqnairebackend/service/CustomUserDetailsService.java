package com.maple.quickqnairebackend.service;

/**
 * Created by zong chang on 2024/12/3 17:01
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.maple.quickqnairebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;  // 假设你有一个用户数据访问层

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息，这里以一个简单的 UserRepository 为例
        Optional<com.maple.quickqnairebackend.entity.User> userEntity = userRepository.findByUsername(username);  // 假设你有一个 UserEntity 类

        if (!userEntity.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
        // 构造 UserDetails，返回给 Spring Security
        return User.builder()
                .username(userEntity.get().getUsername())
                .password(userEntity.get().getPassword())
                .roles(String.valueOf(userEntity.get().getRole()))  // 假设 roles 是一个字符串数组
                .build();
    }
}
