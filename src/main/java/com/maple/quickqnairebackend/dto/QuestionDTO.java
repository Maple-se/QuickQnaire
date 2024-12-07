package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by zong chang on 2024/12/5 1:45
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
// 问题DTO，包含问题和选项信息
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long questionId;  // 问题ID，用于更新现有问题,更新时可以为空

    @NotNull(message = "问题内容不能为空")
    private String questionContent;  // 问题内容

    @Enumerated(EnumType.STRING)
    @NotNull(message = "问题类型不能为空")
    private Question.QuestionType questionType;  // 问题类型（单选、多选等）

    @NotNull(message = "是否必答不能为空")
    private Boolean required; // 是否为必答问题

    private List<OptionDTO> options;  // 问题选项，若为TEXT类型，则此列表为空
}
