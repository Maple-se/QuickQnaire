package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.entity.Survey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveySimpleInfoDTO {
    private Long id;
    private String title;
    private String description;
    private Survey.SurveyStatus status;//问卷状态字段
    private Survey.AccessLevel accessLevel;  // 问卷访问权限
    private Date updatedAt;
}
