package com.maple.quickqnairebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String optionContent;  // 选项内容
}
