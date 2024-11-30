package com.maple.quickqnairebackend.entity;

/**
 * Created by zong chang on 2024/11/29 16:34
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey; // 每个问题属于一个问卷

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 问题内容

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type; // 问题类型（单选、多选、文本）

    @Column(nullable = false)
    private Boolean required; // 是否为必答问题

//    @Column(nullable = false, updatable = false)
//    private Date createdAt;
//
//    @Column(nullable = false)
//    private Date updatedAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Option> options; // 一个问题有多个选项

//    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Answer> answers; // 一个问题有多个回答记录

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuestionResult> questionResults;  // 该问题对应的所有回答


    public enum QuestionType {
        SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT
    }


}

