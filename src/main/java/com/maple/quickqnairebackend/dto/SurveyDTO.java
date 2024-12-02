package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.entity.Survey;

/**
 * Created by zong chang on 2024/12/1 19:59
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//ToDo:SurveyDTO有待进一步完善
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDTO {
    private Long id;
    private String title;
    private String description;
    private Survey.SurveyStatus status;//问卷状态字段
    private String message; // 操作成功或失败的消息
}
