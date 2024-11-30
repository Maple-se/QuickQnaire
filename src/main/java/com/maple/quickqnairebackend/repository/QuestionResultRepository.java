package com.maple.quickqnairebackend.repository;

/**
 * Created by zong chang on 2024/11/30 19:03
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.entity.QuestionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionResultRepository extends JpaRepository<QuestionResult, Long> {
    // 可以在这里添加自定义查询方法
}