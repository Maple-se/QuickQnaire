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
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.service.SurveyResultService;
import com.maple.quickqnairebackend.service.UserService;
import com.maple.quickqnairebackend.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quickqnaire")
public class SurveyResultController {

    @Autowired
    private SurveyResultService surveyResultService;

    @Autowired
    private UserService userService;

    // 提交问卷结果
    //ToDo:问卷结果提交待检查
    @PostMapping("/submit-survey")
    public ResponseEntity<?> submitSurveyResult(@RequestHeader(value = "Authorization", required = false) String authorization,
                                                           @RequestBody SurveyResultDTO surveyResultDTO) {
        String token = null;
        if (authorization != null) {
            token = authorization.replace("Bearer ", ""); // 如果有 token，去除前缀
        }

        if (token != null) {
            Long userId = JwtTokenUtil.extractUserId(token);  // 从 token 中提取用户 ID
            User user = userService.getUserById(userId);
            //surveyResult.setUser(user);  // 将用户关联到问卷结果
        }

        // 保存 SurveyResult，JPA 会自动调用 @PrePersist 方法
        //SurveyResult savedSurveyResult = surveyResultService.saveSurveyResult(surveyResult);

        //ToDo
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");  // 返回保存后的 SurveyResult
    }


    // 获取用户已提交的问卷结果
    @GetMapping("/get")
    public ResponseEntity<SurveyResult> getSurveyResult(@RequestParam Long surveyId,
                                                        @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = JwtTokenUtil.extractUserId(token);

        // 获取问卷结果
        SurveyResult surveyResult = surveyResultService.getSurveyResult(surveyId, userId);
        if (surveyResult != null) {
            return ResponseEntity.ok(surveyResult);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

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
