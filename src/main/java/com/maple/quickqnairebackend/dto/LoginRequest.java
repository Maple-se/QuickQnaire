package com.maple.quickqnairebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by zong chang on 2024/11/30 22:04
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull(message = "密码不能为空")
    private String username;
    @NotNull(message = "密码不能为空")
    private String password;
}