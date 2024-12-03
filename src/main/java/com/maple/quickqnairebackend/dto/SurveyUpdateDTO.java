package com.maple.quickqnairebackend.dto;

/**
 * Created by zong chang on 2024/12/4 0:58
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.entity.Survey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyUpdateDTO {
    private String title;  // 问卷标题
    private String description;  // 问卷描述
    private Survey.AccessLevel accessLevel;  // 问卷访问权限

    private Integer userSetDuration;  // 用户自定义的持续时间（小时）
    private Integer maxResponses;  // 用户自定义的最大响应数

    private List<QuestionDTO> questions;  // 问卷中的问题列表

    // 可以添加其他字段，如问卷的初步设置

    // 问题DTO，包含问题和选项信息
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDTO {
        private Long id;  // 问题ID，用于更新现有问题
        private String questionText;  // 问题内容
        private Question.QuestionType questionType;  // 问题类型（单选、多选等）

        private Boolean required; // 是否为必答问题

        private List<QuestionOptionDTO> options;  // 该问题的选项列表

        // 选项DTO，包含选项内容
        @Setter
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class QuestionOptionDTO {
            private Long id;  // 选项ID，用于更新现有选项
            private String optionText;  // 选项内容
        }
    }
}

