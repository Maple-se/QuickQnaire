package com.maple.quickqnairebackend.dto;

/**
 * Created by zong chang on 2024/12/4 0:50
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

import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyCreationDTO {
    @NotNull(message = "问卷标题不能为空")
    private String title;  // 问卷标题

    @NotNull(message = "问卷描述不能为空")
    private String description;  // 问卷描述

    @NotNull(message = "问卷访问权限不能为空")
    private Survey.AccessLevel accessLevel;  // 问卷访问权限

    private Integer userSetDuration;  // 用户自定义的持续时间（小时）
    private Integer maxResponses;  // 用户自定义的最大响应数

    private List<QuestionCreationDTO> questions;  // 问卷中的问题列表

}

