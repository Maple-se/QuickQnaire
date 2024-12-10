package com.maple.quickqnairebackend.dto;

/**
 * Created by zong chang on 2024/12/4 1:44
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResultDTO {

    private Long surveyId;  // 关联的 Survey 的 ID
    private Long userId;  // 关联的 User 的 ID，匿名用户可以为空
    private List<QuestionResultDTO> questionResults;  // 用户填写的所有问题答案列表

}
