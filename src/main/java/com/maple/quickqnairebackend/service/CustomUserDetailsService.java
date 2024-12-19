package com.maple.quickqnairebackend.service;

/**
 * Created by zong chang on 2024/12/3 17:01
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.maple.quickqnairebackend.dto.CustomUserDetails;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息，这里以一个简单的 UserRepository 为例
        Optional<User> userEntity = userRepository.findByUsername(username);  // 假设你有一个 UserEntity 类

        if (!userEntity.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }

        // 获取用户角色，假设 roles 是一个字符串数组
        String[] roles = new String[]{String.valueOf(userEntity.get().getRole())};  // 获取角色

        // 构造 UserDetails，返回给 Spring Security
        return CustomUserDetails.builder()
                .username(userEntity.get().getUsername())
                .password(userEntity.get().getPassword())
                .userId(userEntity.get().getId())
                .roles(roles)
                .credentialsNonExpired(true)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .build();
    }
}
