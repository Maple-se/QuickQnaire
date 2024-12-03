package com.maple.quickqnairebackend.controller;

import com.maple.quickqnairebackend.dto.SurveyCreationDTO;
import com.maple.quickqnairebackend.dto.SurveySimpleInfoDTO;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.service.SurveyService;
import com.maple.quickqnairebackend.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Base64;

/**
 * Created by zong chang on 2024/12/1 20:04
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@RestController
@RequestMapping("/quickqnaire")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    //创建问卷
    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createSurvey(@RequestHeader("Authorization") String authorization,@Valid @RequestBody SurveyCreationDTO surveyCreationDTO){
        try{
            // 从 Authorization 头中获取 token 并解析用户信息
            String token = authorization.replace("Bearer ", "");
            Long userId = JwtTokenUtil.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
            }
            Survey survey=surveyService.toEntity(surveyCreationDTO,userId);
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.createSurvey(survey,userId);
            return ResponseEntity.ok(surveySimpleInfoDTO);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Create Survey Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    // 根据用户ID获取所有问卷
    @GetMapping("/survey-list")
    public ResponseEntity<?> getSurveysByUserId(@RequestHeader("Authorization") String authorization) {
        try {
            // 从 Authorization 头中获取 token 并解析用户信息
            String token = authorization.replace("Bearer ", "");
            Long userId = JwtTokenUtil.extractUserId(token);
            List<SurveySimpleInfoDTO> surveyDTOs = surveyService.getSurveysByUserId(userId);
            return ResponseEntity.ok(surveyDTOs);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Get Surveys By UserId Error");
        }
    }

    // 获取问卷信息，surveyId 通过 Base64 编码并传递

    @Transactional
    @GetMapping("/public-survey/{encodedSurveyId}")
    public ResponseEntity<?> getSurveyById(@PathVariable String encodedSurveyId) {
        try {
            // 解码 Base64 编码的 surveyId
            String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
            Long surveyId = Long.parseLong(decodedId); // 转换为 Long 类型

            // 根据解码后的 surveyId 获取问卷
            Survey survey = surveyService.getSurveyById(surveyId);
            return ResponseEntity.ok(survey);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Survey ID");
        }
    }
}
