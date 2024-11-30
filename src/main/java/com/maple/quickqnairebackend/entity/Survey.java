package com.maple.quickqnairebackend.entity;

/**
 * Created by zong chang on 2024/11/29 16:31
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurveyStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Column(nullable = false)
    private Date updatedAt;

    private Date activeStartDate;  // 问卷被管理员批准并发布的时间（`ACTIVE` 状态开始时间）

    private Integer duration;  // 系统默认持续时间（单位：小时）
    private Integer userSetDuration;  // 用户自定义持续时间（单位：小时）
    private Integer maxResponses;  // 最大回答数

    @Column(nullable = false)
    private Integer responsesReceived = 0;  // 已收到的回答数

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions; // 每个问卷包含多个问题

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SurveyResult> surveyResults;  // 一个 Survey 有多个 SurveyResult

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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


    // 自定义方法：批准并发布问卷
    public void approve() {
        if (this.status == SurveyStatus.PENDING_APPROVAL) {
            this.status = SurveyStatus.ACTIVE;
            this.activeStartDate = new Date();  // 设置问卷被批准后的开始时间
        }
    }

    // 自定义方法：关闭问卷（例如，时间到期或回答数已满）
    public void close() {
        if (this.status == SurveyStatus.ACTIVE || this.status == SurveyStatus.PENDING_APPROVAL) {
            this.status = SurveyStatus.CLOSED;
        }
    }

    // 自定义方法：检查问卷是否已经结束
    public void checkSurveyEnd() {
        if (this.status == SurveyStatus.CLOSED) {
            return;
        }

        // 1. 用户手动关闭问卷（优先级最高）
//        if (this.status == SurveyStatus.PENDING_APPROVAL || this.status == SurveyStatus.ACTIVE) {
//            if (isUserSetDurationExpired()) {
//                close();  // 用户自定义结束时间，关闭问卷
//                return;
//            }
//        }

        boolean shouldClose = false;

        // 1. 达到最大回答数
        if (this.maxResponses != null && this.responsesReceived >= this.maxResponses) {
            shouldClose = true;  // 满足最大回答数条件
        }

        // 2. 达到用户设置的持续时间
        if (this.userSetDuration != null && isUserSetDurationExpired()) {
            shouldClose = true;  // 满足用户设置持续时间条件
        }


        // 4. 达到系统默认的持续时间
        if (this.duration != null && this.activeStartDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(this.activeStartDate);  // 从`ACTIVE`开始时间计算
            calendar.add(Calendar.HOUR, this.duration);
            Date systemDefaultEnd = calendar.getTime();
            if (new Date().after(systemDefaultEnd)) {
                close();  // 达到系统默认的持续时间，关闭问卷
                return;
            }
        }

        // 如果满足任何一个条件，提示用户是否关闭问卷
        if (shouldClose) {
            promptUserToCloseSurvey();
        }
    }

    // 检查用户设置的持续时间是否已过
    private boolean isUserSetDurationExpired() {
        if (this.userSetDuration == null || this.status != SurveyStatus.ACTIVE || this.activeStartDate == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.activeStartDate);  // 从`ACTIVE`开始时间计算
        calendar.add(Calendar.HOUR, this.userSetDuration);  // 增加用户设置的小时数
        Date userSetEnd = calendar.getTime();

        return new Date().after(userSetEnd);  // 如果当前时间已超过结束时间，则返回 true
    }


    private void promptUserToCloseSurvey() {
        // 这里返回给前端提示，用户可以选择关闭问卷
        // 可以考虑返回一个提示消息，等待用户确认是否关闭
        // 例如： "问卷已经满足条件，是否关闭问卷？"
    }
    // 强制关闭问卷（管理员权限）
    public void forceClose() {
        close();  // 管理员强制关闭问卷
    }
}

