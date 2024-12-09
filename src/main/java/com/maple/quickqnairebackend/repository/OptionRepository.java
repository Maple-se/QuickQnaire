package com.maple.quickqnairebackend.repository;

/**
 * Created by zong chang on 2024/11/30 19:02
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findByQuestionId(Long questionId);

    // 判断特定 Question 中是否存在指定选项
    boolean existsByIdAndQuestionId(Long optionId, Long questionId);
}
