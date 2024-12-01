package com.maple.quickqnairebackend.controller;

import com.maple.quickqnairebackend.dto.SurveyDTO;
import com.maple.quickqnairebackend.service.SurveyService;
import com.maple.quickqnairebackend.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zong chang on 2024/12/1 20:04
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@RestController
@RequestMapping("/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    // 根据用户ID获取所有问卷
    @GetMapping("/surveylist")
    public ResponseEntity<List<SurveyDTO>> getSurveysByUserId(@RequestHeader("Authorization") String authorization) {
        // 从 Authorization 头中获取 token 并解析用户信息
        String token = authorization.replace("Bearer ", "");

        // 解析 token 获取角色
        //String role = ;
        Long userId = JwtTokenUtil.extractUserId(token);
        List<SurveyDTO> surveyDTOs = surveyService.getSurveysByUserId(userId);
        return ResponseEntity.ok(surveyDTOs);
    }
}
