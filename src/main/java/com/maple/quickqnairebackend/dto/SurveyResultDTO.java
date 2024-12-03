package com.maple.quickqnairebackend.dto;

/**
 * Created by zong chang on 2024/12/4 1:44
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResultDTO {

    private Long surveyId;  // 问卷ID
    private Long userId;    // 用户ID

    private List<QuestionResponseDTO> questionResponses;  // 问题和对应的答案列表

    // 内部类：问题的答案
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponseDTO {
        private Long questionId;  // 问题ID
        private String questionContent;  // 问题内容

        private List<String> answers;  // 用户的答案：可以是文本、选项值等
    }
}
