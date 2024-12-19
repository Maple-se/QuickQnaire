package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.validation.SurveyCreateGroup;
import com.maple.quickqnairebackend.validation.SurveyUpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Created by zong chang on 2024/12/5 1:46
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
// 选项DTO，包含选项内容
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {
    private Long optionId;  // 选项ID，用于更新现有选项

    @NotBlank(groups = {SurveyCreateGroup.class, SurveyUpdateGroup.class},message = "选项内容不能为空")
    @Size(min = 2,max = 50,groups = {SurveyCreateGroup.class, SurveyUpdateGroup.class}, message = "选项内容不少于2字符，不超过100字符")
    private String optionContent;  // 选项内容
}
