package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by zong chang on 2024/12/4 1:12
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50之间")
    private String username;


    @NotNull(message = "密码不能为空")
    private String password;


    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    // 用户角色不能为空，只能是 ADMIN 或 USER
    @Enumerated(EnumType.STRING)
    @NotNull(message = "角色不能为空")
    private User.Role role;
}
