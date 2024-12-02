package com.maple.quickqnairebackend.entity;

/**
 * Created by zong chang on 2024/11/29 16:21
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    @NotNull(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50之间")
    @Setter
    private String username;

    @Column(nullable = false)
    @NotNull(message = "密码不能为空")
    //@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$", message = "密码必须包含字母和数字，且长度至少为6个字符")
    @Setter
    private String password;

    @Column(length = 100, unique = true)
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Setter
    private String email;

    // 用户角色不能为空，只能是 ADMIN 或 USER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "角色不能为空")
    @Setter
    private Role role;

    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @Column(nullable = false)
    private Date updatedAt;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Answer> answers; // 一个用户有多个回答记录
//
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SurveyResult> surveyResults;  // 一个用户可以填写多个Survey

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Survey> surveys;//一个用户可以创建多个Survey

    public enum Role {
        ADMIN, USER
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
}
