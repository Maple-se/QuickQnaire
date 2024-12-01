package com.maple.quickqnairebackend.repository;

/**
 * Created by zong chang on 2024/11/30 19:01
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findByStatus(Survey.SurveyStatus status);  // 根据状态查找问卷

    // 根据用户查找该用户创建的所有问卷
    List<Survey> findByCreatedBy(User createdBy);
}