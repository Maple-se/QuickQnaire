package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Long questionId;  // 问题ID，用于更新现有问题

    private String questionContent;  // 问题内容

    private Question.QuestionType questionType;  // 问题类型（单选、多选等）

    private Boolean required; // 是否为必答问题

    private List<OptionDTO> options;  // 问题选项，使用 BaseOptionDTO 来支持选项的不同类型
}
