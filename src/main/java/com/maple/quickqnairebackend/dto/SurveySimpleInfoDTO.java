package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.entity.Survey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveySimpleInfoDTO {
    private Long id;
    private String title;
    private String description;
    private Survey.SurveyStatus status;//问卷状态字段
    //private String message; // 操作成功或失败的消息
}
