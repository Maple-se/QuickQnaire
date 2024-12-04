package com.maple.quickqnairebackend.dto;

/**
 * Created by zong chang on 2024/12/4 17:21
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDetailDTO {
    private Long surveyId;
    private String title;
    private String description;
    private List<QuestionDetailDTO> questions;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDetailDTO {
        private Long questionId;
        private String questionContent;
        private Question.QuestionType questionType; // 问题类型（单选、多选等）
        private List<OptionDetailDTO> options;

        @Setter
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OptionDetailDTO {
            private Long optionId;
            private String optionContent; // 选项内容
        }
    }
}