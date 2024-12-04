package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by zong chang on 2024/12/4 12:55
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
public class QuestionCreationDTO {
    @NotNull(message = "问题内容不能为空")
    private String questionContent;  // 问题内容

    @NotNull(message = "问题类型不能为空")
    private Question.QuestionType questionType;  // 问题类型（单选、多选等）

    @NotNull(message = "是否必搭不能为空")
    private Boolean required; // 是否为必答问题

    private List<QuestionOptionCreationDTO> options;  // 该问题的选项列表

}
