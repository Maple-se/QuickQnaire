package com.maple.quickqnairebackend.controller;

/**
 * Created by zong chang on 2024/12/3 18:43
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.maple.quickqnairebackend.dto.SurveyResultDTO;
import com.maple.quickqnairebackend.entity.SurveyResult;
import com.maple.quickqnairebackend.service.SurveyResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quickqnaire")
public class SurveyResultController {

    private final SurveyResultService surveyResultService;


    //ToDo:问卷提交的权限控制
    // 提交问卷结果
    //允许：问卷状态：Active，游客：Public访问权限的问卷可以提交，授权用户：既可以提交Public问卷也可提交Private问卷
    @PostMapping("/submit-survey")
    public ResponseEntity<?> submitSurveyResult(@Validated @RequestBody SurveyResultDTO surveyResultDTO) {

       SurveyResult surveyResult = surveyResultService.saveSurveyResult(surveyResultDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(surveyResult);  // 返回保存后的 SurveyResult
    }


    // 获取用户已提交的问卷结果
//    @GetMapping("/getAnswer")
//    public ResponseEntity<SurveyResult> getSurveyResult(@RequestParam Long surveyId) {
//
//        // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 user
//        // 获取问卷结果
//        SurveyResult surveyResult = surveyResultService.getSurveyResult(surveyId, userId);
//        if (surveyResult != null) {
//            return ResponseEntity.ok(surveyResult);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    // 获取匿名用户的问卷结果
//    @GetMapping("/getAnonymous")
//    public ResponseEntity<SurveyResult> getSurveyResultForAnonymous(@RequestParam String surveyId,
//                                                                    @RequestParam String anonymousId) {
//        SurveyResult surveyResult = surveyResultService.getSurveyResultForAnonymous(surveyId, anonymousId);
//        if (surveyResult != null) {
//            return ResponseEntity.ok(surveyResult);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
