package com.maple.quickqnairebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by zong chang on 2024/12/4 12:56
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
public class QuestionOptionCreationDTO {
    @NotNull(message = "选项内容不能为空")
    private String optionContent;  // 选项内容
}
