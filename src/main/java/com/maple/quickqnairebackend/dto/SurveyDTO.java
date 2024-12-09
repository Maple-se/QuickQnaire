package com.maple.quickqnairebackend.dto;

/**
 * Created by zong chang on 2024/12/4 0:58
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.validation.SurveyCreateGroup;
import com.maple.quickqnairebackend.validation.SurveyUpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDTO {
    //创建问卷时，不需要提供id
    //更新时需要检查id
    @NotNull(groups = SurveyUpdateGroup.class, message = "问卷ID不能为空")
    private Long surveyId;  // 问卷ID

    @NotBlank(groups = {SurveyCreateGroup.class, SurveyUpdateGroup.class}, message = "问卷标题不能为空")
    @Size(max = 100, groups = {SurveyCreateGroup.class, SurveyUpdateGroup.class}, message = "问卷标题长度不能超过100个字符")
    private String title;  // 问卷标题

    @NotNull(groups = {SurveyCreateGroup.class, SurveyUpdateGroup.class}, message = "问卷描述不能为空")
    @Size(max = 200, groups = {SurveyCreateGroup.class, SurveyUpdateGroup.class}, message = "问卷描述不超过200个字符")
    private String description;  // 问卷描述

    //ToDo:枚举字段验证待考虑
    @NotNull(groups = SurveyCreateGroup.class, message = "问卷访问权限不能为空")
    private Survey.AccessLevel accessLevel;  // 问卷访问权限

    private Integer userSetDuration;  // 用户自定义的持续时间（小时）

    private Integer maxResponses;  // 用户自定义的最大响应数

    @Valid// 嵌套验证必须用@Valid
    @NotEmpty(groups = SurveyCreateGroup.class,message = "问卷列表不能为空")
    private List<QuestionDTO> questions;  // 问卷中的问题列表
}

