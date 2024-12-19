package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.validation.SurveyCreateGroup;
import com.maple.quickqnairebackend.validation.SurveyUpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by zong chang on 2024/12/5 1:45
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
// 问题DTO，包含问题和选项信息
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long questionId;  // 问题ID,更新时可以为空

    @NotBlank(groups = {SurveyCreateGroup.class, SurveyUpdateGroup.class},message = "问题内容不能为空")
    @Size(min = 2,max = 100,groups = {SurveyCreateGroup.class, SurveyUpdateGroup.class}, message = "问题内容不少于2字符，不超过100字符")
    private String questionContent;  // 问题内容

    //ToDo:枚举类型校验需要做进一步细化
    @NotNull(groups = {SurveyCreateGroup.class, SurveyUpdateGroup.class},message = "问题类型不能为空")
    private Question.QuestionType type;  // 问题类型（单选、多选等）

    @NotNull(groups = {SurveyCreateGroup.class, SurveyUpdateGroup.class},message = "是否必答不能为空")
    private Boolean required; // 是否为必答问题

    @Valid
    private List<OptionDTO> options;  // 问题选项，若为TEXT类型，则此列表为空
}
