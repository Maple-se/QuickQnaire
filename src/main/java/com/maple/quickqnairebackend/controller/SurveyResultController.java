package com.maple.quickqnairebackend.controller;

/**
 * Created by zong chang on 2024/12/3 18:43
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.maple.quickqnairebackend.dto.SurveyResultDTO;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.SurveyResult;
import com.maple.quickqnairebackend.service.SurveyResultService;
import com.maple.quickqnairebackend.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quickqnaire")
public class SurveyResultController {

    private final SurveyResultService surveyResultService;

    private final SurveyService surveyService;


    /*
    * Active问卷
    * Public 问卷可允许任何用户提交
    * Private 问卷仅允许登录用户提交
    *
    * */
    @PreAuthorize(
            "@surveyPermission.adminAndActive()" +
            " or @surveyPermission.checkSurveyAccessLevel()"
    )
    @PostMapping("/submit-survey/{encodedSurveyId}")
    public ResponseEntity<?> submitSurveyResult(@Validated @RequestBody SurveyResultDTO surveyResultDTO , @PathVariable String encodedSurveyId) {

        SurveyResult surveyResult = surveyResultService.saveSurveyResult(surveyResultDTO);
        Long surveyId = surveyService.getDecodedSurveyId(encodedSurveyId);
//        Survey survey = surveyService.getSurveyById(surveyId);
//        survey.setResponsesReceived(survey.getResponsesReceived() + 1);
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
