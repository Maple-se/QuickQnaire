package com.maple.quickqnairebackend.repository;

/**
 * Created by zong chang on 2024/11/30 19:03
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    // 根据 Survey ID 和 User ID 查找提交的问卷结果
    Optional<SurveyResult> findBySurveyIdAndUserId(Long surveyId, Long userId);

    // 根据 Survey ID 和 匿名 ID 查找匿名用户提交的问卷结果
    //Optional<SurveyResult> findBySurveyIdAndAnonymousId(String surveyId, String anonymousId);


    // 根据 User ID 查找所有提交的问卷结果
    List<SurveyResult> findByUserId(Long userId);
}
