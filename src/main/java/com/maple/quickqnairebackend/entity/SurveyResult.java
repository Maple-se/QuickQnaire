package com.maple.quickqnairebackend.entity;

/**
 * Created by zong chang on 2024/11/29 16:55
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey; // 关联的问卷

    @Column(nullable = false)
    private int totalResponses; // 总回答数

    // 其他统计字段可根据需求添加
}