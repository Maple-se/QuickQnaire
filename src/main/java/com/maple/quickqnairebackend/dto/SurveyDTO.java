package com.maple.quickqnairebackend.dto;

/**
 * Created by zong chang on 2024/12/4 0:58
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

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
public class SurveyDTO {
    //创建问卷时，不需要提供id
    //更新时需要检查id
    private Long surveyId;  // 问卷ID

    private String title;  // 问卷标题

    private String description;  // 问卷描述

    private Survey.AccessLevel accessLevel;  // 问卷访问权限

    private Integer userSetDuration;  // 用户自定义的持续时间（小时）

    private Integer maxResponses;  // 用户自定义的最大响应数

    private List<QuestionDTO> questions;  // 问卷中的问题列表
}

