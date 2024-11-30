package com.maple.quickqnairebackend.repository;

/**
 * Created by zong chang on 2024/11/30 19:02
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    // 可以在这里添加自定义查询方法
}
