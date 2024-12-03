package com.maple.quickqnairebackend.entity;

/**
 * Created by zong chang on 2024/11/29 16:34
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    @JsonBackReference  // 使 Question -> Survey 的反向引用被忽略
    private Survey survey; // 每个问题属于一个问卷

    @Setter
    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionContent; // 问题内容

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type; // 问题类型（单选、多选、文本）

    @Setter
    @Column(nullable = false)
    private Boolean required; // 是否为必答问题

//    @Column(nullable = false, updatable = false)
//    private Date createdAt;
//
//    @Column(nullable = false)
//    private Date updatedAt;

    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference  // 使 Question -> Option 的序列化正常进行
    private List<QuestionOption> options; // 一个问题有多个选项

//    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Answer> answers; // 一个问题有多个回答记录

    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<QuestionResult> questionResults;  // 该问题对应的所有回答


    public enum QuestionType {
        SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT
    }


    @Override
    public String toString() {
        // 输出问题内容和选项列表
        return "Question{id=" + id + ", content='" + questionContent + "', options=" + options + "}";
    }


}

