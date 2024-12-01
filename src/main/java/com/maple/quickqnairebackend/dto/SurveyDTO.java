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
import lombok.NoArgsConstructor;

//ToDo:SurveyDTO有待进一步完善
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDTO {
    private Long id;
    private String title;
    private Survey.SurveyStatus status;
}
