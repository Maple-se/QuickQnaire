package com.maple.quickqnairebackend.repository;

/**
 * Created by zong chang on 2024/11/30 19:10
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 你可以在这里添加自定义查询方法，例如根据用户名查找用户
    Optional<User> findByUsername(String username);

    // 通过用户名查询
    boolean existsByUsername(String username);

    // 通过邮箱查询
    boolean existsByEmail(String email);
}
