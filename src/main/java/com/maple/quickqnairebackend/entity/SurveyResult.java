package com.maple.quickqnairebackend.entity;

/**
 * Created by zong chang on 2024/11/29 16:55
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
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // SurveyResult 的唯一标识

    @Setter
    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    @JsonBackReference
    private Survey survey;  // 关联的 Survey

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)  // user_id 可能为空，对于匿名用户
    @JsonBackReference
    private User user;  // 关联的 User

    @Column(nullable = true)
    private Boolean anonymousId = false;  // 匿名用户标识，对于匿名问卷使用

    @Setter
    @OneToMany(mappedBy = "surveyResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<QuestionResult> questionResults;  // 该用户填写的该问卷中的所有问题答案

    @Column(nullable = false)
    private Date submittedAt;  // 提交时间

    // 在问卷提交前，设置提交时间
    @PrePersist
    public void prePersist() {
        if (this.submittedAt == null) {
            this.submittedAt = new Date();
        }

        // 如果是匿名用户，生成一个匿名ID
        if (this.user == null) {
            this.anonymousId = true;  // 使用 UUID 生成唯一的匿名标识
        }
    }
}
