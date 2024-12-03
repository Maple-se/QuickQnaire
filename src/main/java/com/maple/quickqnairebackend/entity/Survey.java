package com.maple.quickqnairebackend.entity;

/**
 * Created by zong chang on 2024/11/29 16:31
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    @Setter
    private String title;

    @Column(columnDefinition = "TEXT")
    @Setter
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurveyStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    @Setter
    @JsonBackReference
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Column(nullable = false)
    private Date updatedAt;

    private Date activeStartDate;  // 问卷被管理员批准并发布的时间（`ACTIVE` 状态开始时间）

    @Setter
    private Integer duration;  // 系统默认持续时间（单位：小时）

    @Setter
    private Integer userSetDuration;  // 用户自定义持续时间（单位：小时）
    @Setter
    private Integer maxResponses;  // 用户自定义最大回答数

    @Setter
    @Column(nullable = false)
    private Integer responsesReceived = 0;  // 已收到的回答数

    /*
    * FetchType.LAZY
    * 推荐的方法是使用 LAZY 加载，因为 EAGER 加载可能会导致性能问题
    * */
    @Setter
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Question> questions; // 每个问卷包含多个问题

    @Setter
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference// 使 Survey -> Question 的序列化正常进行
    private List<SurveyResult> surveyResults;  // 一个 Survey 有多个 SurveyResult

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private AccessLevel accessLevel; // Survey实体中添加访问控制级别字段

    public enum AccessLevel {
        PUBLIC, PRIVATE, RESTRICTED
    }

    public enum SurveyStatus {
        DRAFT,             // 草稿状态
        PENDING_APPROVAL,  // 待审核状态
        ACTIVE,            // 已发布状态
        CLOSED             // 已关闭状态
    }

    // 在实体类中添加生命周期回调方法

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = new Date();
        }
        this.updatedAt = new Date();  // 每次插入时都会设置更新日期
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();  // 每次更新时都会设置更新时间
    }

    // 自定义方法：用户创建问卷
    public void create() {
        this.status = SurveyStatus.DRAFT;
    }


    // 自定义方法：用户提交问卷给管理员审核，问卷状态变更
    public void submit() {
        if (this.status == SurveyStatus.DRAFT) {
            this.status = SurveyStatus.PENDING_APPROVAL;
        }
    }


    // 自定义方法：批准并发布问卷
    public void approve() {
        if (this.status == SurveyStatus.PENDING_APPROVAL) {
            this.status = SurveyStatus.ACTIVE;
            this.activeStartDate = new Date();  // 设置问卷被批准后的开始时间
        }
    }

    // 自定义方法：拒绝问卷
    public void reject() {
        if (this.status == SurveyStatus.PENDING_APPROVAL) {
            this.status = SurveyStatus.DRAFT;  // 如果被拒绝，设置状态为草稿
        }
    }

    // 自定义方法：关闭问卷（例如，时间到期或回答数已满）
    public void close() {
        if (this.status == SurveyStatus.ACTIVE || this.status == SurveyStatus.PENDING_APPROVAL) {
            this.status = SurveyStatus.CLOSED;
        }
    }


    // 检查用户设置的持续时间是否已过
//    private boolean isUserSetDurationExpired() {
//        if (this.userSetDuration == null || this.status != SurveyStatus.ACTIVE || this.activeStartDate == null) {
//            return false;
//        }
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(this.activeStartDate);  // 从`ACTIVE`开始时间计算
//        calendar.add(Calendar.HOUR, this.userSetDuration);  // 增加用户设置的小时数
//        Date userSetEnd = calendar.getTime();
//
//        return new Date().after(userSetEnd);  // 如果当前时间已超过结束时间，则返回 true
//    }

    // 强制关闭问卷（管理员权限）
//    public void forceClose() {
//        close();  // 管理员强制关闭问卷
//    }
}

