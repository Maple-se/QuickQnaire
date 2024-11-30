package com.maple.quickqnairebackend.entity;

/**
 * Created by zong chang on 2024/11/30 15:40
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 唯一标识

    @ManyToOne
    @JoinColumn(name = "survey_result_id", nullable = false)
    private SurveyResult surveyResult;  // 关联的 SurveyResult

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;  // 关联的 Question

    @Column(nullable = false)
    private String answer;  // 问题的答案，类型可以根据需要调整

}

