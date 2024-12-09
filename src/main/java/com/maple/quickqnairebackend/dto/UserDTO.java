package com.maple.quickqnairebackend.dto;

import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.validation.UserCreateGroup;
import com.maple.quickqnairebackend.validation.UserUpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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

    //更新时需要检查id
    @NotNull(groups = UserUpdateGroup.class, message = "用户ID不能为空")
    private Long id;

    @NotBlank(groups = UserCreateGroup.class,message = "用户名不能为空")
    @Size(min = 3, max = 50, groups = {UserCreateGroup.class,UserUpdateGroup.class},message = "用户名长度必须在3到50之间")
    private String username;


    @NotBlank(groups = UserCreateGroup.class,message = "密码不能为空")
    @Size(min = 6, groups = UserCreateGroup.class, message = "密码长度必须不少于6位")
    private String password;


    @NotNull(groups = UserCreateGroup.class,message = "邮箱不能为空")
    @Email(groups = {UserCreateGroup.class,UserUpdateGroup.class},message = "邮箱格式不正确")
    private String email;

    //ToDo:枚举类型校验待考虑
    // 用户角色不能为空，只能是 ADMIN 或 USER
    @NotNull(groups = UserCreateGroup.class,message = "角色不能为空")
    private User.Role role;
}
