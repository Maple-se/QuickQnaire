package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.validation.SurveyUpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by zong chang on 2024/12/11 0:54
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDTO {
    @NotNull(message = "问题ID不能为空")
    private Long questionId;  // 关联的 Question 的 ID
    private Set<Long> selectedOptionIds;  // 对于单选和多选问题，存储选中的选项 ID 集合
    private String textAnswer;  // 对于文本问题，存储文本答案
    @NotNull(message = "是否回答了该问题不能为空")
    private Boolean requiredAnswered;  // 记录是否回答了该问题
}
