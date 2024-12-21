package com.maple.quickqnairebackend.entity;

/**
 * Created by zong chang on 2024/11/30 15:40
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 唯一标识

    @Setter
    @ManyToOne
    @JoinColumn(name = "survey_result_id", nullable = false)
    @JsonBackReference
    private SurveyResult surveyResult;  // 关联的 SurveyResult

    @Setter
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private Question question;  // 关联的 Question

    // 单选题或多选题的选项结果（存储选项的ID）
    @Setter
    @ElementCollection
    @CollectionTable(name = "question_result_options", joinColumns = @JoinColumn(name = "question_result_id"))
    @Column(name = "option_id")
    private Set<Long> selectedOptionIds;  // 对于单选和多选问题，存储选中的选项ID

    @Setter
    @Column(columnDefinition = "TEXT")
    private String textAnswer;  // 对于文本问题，存储文本答案

    @Setter
    @Column(nullable = false)
    private Boolean requiredAnswered;  // 记录是否回答了该问题

}

