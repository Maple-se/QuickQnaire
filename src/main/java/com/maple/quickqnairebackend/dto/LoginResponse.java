package com.maple.quickqnairebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by zong chang on 2024/11/30 22:04
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String message;
}
