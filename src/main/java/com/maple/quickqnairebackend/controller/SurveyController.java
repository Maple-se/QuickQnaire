package com.maple.quickqnairebackend.controller;

import com.maple.quickqnairebackend.dto.*;
import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.service.OptionService;
import com.maple.quickqnairebackend.service.QuestionService;
import com.maple.quickqnairebackend.service.SurveyService;
import com.maple.quickqnairebackend.service.UserService;
import com.maple.quickqnairebackend.validation.SurveyCreateGroup;
import com.maple.quickqnairebackend.validation.SurveyUpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private QuestionService questionService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private UserService userService;

    //创建问卷
    //创建问卷API测试通过
    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createSurvey(@Validated(SurveyCreateGroup.class) @RequestBody SurveyDTO surveyCreationDTO){
        try{
            // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 userId
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.createSurvey(surveyCreationDTO,userId);
            for (QuestionDTO qdto: surveyCreationDTO.getQuestions()) {
                Question createdQuestion = questionService.createQuestion(surveySimpleInfoDTO.getId(),qdto);
                // 根据问题类型，创建选项（如果是单选或多选类型）
                if (qdto.getQuestionType() == Question.QuestionType.SINGLE_CHOICE || qdto.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE) {
                    for (OptionDTO odto: qdto.getOptions()) {
                        optionService.createOption(createdQuestion.getId(),odto);
                    }
                }
            }
            return ResponseEntity.ok(surveySimpleInfoDTO);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Create Survey Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }


    @GetMapping("/preview/{encodedSurveyId}")
    public ResponseEntity<?> previewSurvey(@PathVariable String encodedSurveyId){
        // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 user
        User user = userService.getUserById(userId);
        // 解码 Base64 编码的 surveyId
        String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
        Long surveyId;
        try {
            surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid survey ID format.");
        }
        // 根据解码后的 surveyId 获取问卷
        Survey survey = surveyService.getSurveyById(surveyId);
        //问卷创建者才可以更新
        surveyService.validateSurveyOwnership(survey,user);
        return ResponseEntity.ok(surveyService.toSurveyDTO(survey));
    }

    //更新问卷
    //API测试通过
    @Transactional
    @PutMapping("/update-survey")
    public ResponseEntity<?> updateSurveyDetail(@Validated(SurveyUpdateGroup.class) @RequestBody SurveyDTO sdto){
        try {
            // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 userId
            User user = userService.getUserById(userId);
            // 根据解码后的 surveyId 获取问卷
            Survey survey = surveyService.getSurveyById(sdto.getSurveyId());

            //问卷创建者才可以更新
            surveyService.validateSurveyOwnership(survey,user);
            //草稿状态才可以更新
            surveyService.isDraftStatus(survey);
            //增量更新（仅更新变动部分的数据（例如新增、修改），删除逻辑独立出去
            Survey updatedSurvey = surveyService.updateSurvey(survey,sdto);

            // 处理问题列表
            if(sdto.getQuestions() != null){
            // 更新问题列表
            for (QuestionDTO questionDTO : sdto.getQuestions()) {
               Question updatedQuestion= questionService.processQuestion(updatedSurvey, questionDTO);
                if(questionDTO.getOptions() != null) {
                    // 更新选项
                    for (OptionDTO optionDTO : questionDTO.getOptions()) {
                        // 处理选项
                        optionService.processOption(updatedQuestion, optionDTO);
                    }
                }

            }
     }
            return ResponseEntity.ok(surveyService.toSurveyDTO(surveyService.getSurveyById(sdto.getSurveyId())));
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }


    //用户提交问卷，管理员审批
    //API测试通过
    @PutMapping("/submit-for-approval/{encodedSurveyId}")
    public ResponseEntity<?> submitSurveyForApproval( @PathVariable String encodedSurveyId){
       try {
           // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 user
           User user = userService.getUserById(userId);
           // 解码 Base64 编码的 surveyId
           String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
           Long surveyId;
           try {
               surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
           } catch (NumberFormatException e) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid survey ID format.");
           }
           // 根据解码后的 surveyId 获取问卷
           Survey survey = surveyService.getSurveyById(surveyId);
           if(survey.getCreatedBy()!=user){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Identity Error");
           }
           if(survey.getStatus() != Survey.SurveyStatus.DRAFT){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
           }
         SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.submitSurveyForApproval(survey.getId());
           return ResponseEntity.ok(surveySimpleInfoDTO);
       }catch (IllegalArgumentException e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
       }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/test")
    public ResponseEntity<Void> test(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(authority -> System.out.println("Authority: " + authority.getAuthority()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    //ToDo:问卷状态变更存在冗余代码
    //管理员批准问卷，状态变为active
    //API测试通过
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/approval-survey/{encodedSurveyId}")
    public ResponseEntity<?> approvalSurvey(@PathVariable String encodedSurveyId){
        try {
            // 解码 Base64 编码的 surveyId
            String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
            Long surveyId;
            try {
                surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid survey ID format.");
            }
            // 根据解码后的 surveyId 获取问卷
            Survey survey = surveyService.getSurveyById(surveyId);

            if(survey.getStatus() != Survey.SurveyStatus.PENDING_APPROVAL){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.approveSurvey(survey.getId());
            return ResponseEntity.ok(surveySimpleInfoDTO);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //管理员拒绝问卷，状态变为草稿
    //API测试通过
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/reject-survey/{encodedSurveyId}")
    public ResponseEntity<?> rejectSurvey(@PathVariable String encodedSurveyId){
        try {
            // 解码 Base64 编码的 surveyId
            String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
            Long surveyId;
            try {
                surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid survey ID format.");
            }
            // 根据解码后的 surveyId 获取问卷
            Survey survey = surveyService.getSurveyById(surveyId);

            if(survey.getStatus() != Survey.SurveyStatus.PENDING_APPROVAL){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.rejectSurvey(survey.getId());
            return ResponseEntity.ok(surveySimpleInfoDTO);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    //管理员或用户，手动关闭问卷
    //API测试通过
    @PutMapping("/close-survey/{encodedSurveyId}")
    public ResponseEntity<?> closeSurvey(@PathVariable String encodedSurveyId){
        try {
            // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 use
            User user = userService.getUserById(userId);

            // 解码 Base64 编码的 surveyId
            String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
            Long surveyId;
            try {
                surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid survey ID format.");
            }
            // 根据解码后的 surveyId 获取问卷
            Survey survey = surveyService.getSurveyById(surveyId);
            if(survey.getCreatedBy()!=user && user.getRole() != User.Role.ADMIN){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Identity Error");
            }
            if(survey.getStatus() != Survey.SurveyStatus.PENDING_APPROVAL && survey.getStatus() != Survey.SurveyStatus.ACTIVE){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.closeSurvey(survey.getId());
            return ResponseEntity.ok(surveySimpleInfoDTO);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    //删除问卷
    //API测试通过
    @DeleteMapping("/delete-survey/{encodedSurveyId}")
    public ResponseEntity<?> deleteSurvey(@PathVariable String encodedSurveyId){
        try {
            // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 use
            User user = userService.getUserById(userId);
            // 解码 Base64 编码的 surveyId
            String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
            Long surveyId;
            try {
                surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid survey ID format.");
            }
            // 根据解码后的 surveyId 获取问卷
            Survey survey = surveyService.getSurveyById(surveyId);
            if(survey.getCreatedBy()!=user && user.getRole() != User.Role.ADMIN){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Identity Error");
            }
            surveyService.deleteSurvey(survey.getId());
            return ResponseEntity.status(HttpStatus.FOUND).body("Delete Success");  // 删除成功，返回204 No Content
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 问卷不存在
        } catch (Exception e) {
            // 处理其他可能的异常
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // 根据用户ID获取所有问卷
    @GetMapping("/surveys")
    public ResponseEntity<?> getSurveysByUserId() {
        try {
            // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 user
            List<SurveySimpleInfoDTO> surveySimpleInfoDTOS = surveyService.getSurveysByUserId(userId);
            return ResponseEntity.ok(surveySimpleInfoDTOS);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Get Surveys By UserId Error");
        }
    }

    // 获取问卷信息，surveyId 通过 Base64 编码并传递
    //因在antMatchers("/quickqnaire/detail/**").permitAll() 处放开了该API，因此需要对
    // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //上下文对象进行详细的判空处理
    @Transactional
    @GetMapping("/detail/{encodedSurveyId}")
    public ResponseEntity<?> getSurveyById(@PathVariable String encodedSurveyId) {
        try {
            // 解码 Base64 编码的 surveyId
            String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
            Long surveyId;
            try {
                surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid survey ID format.");
            }

            // 根据解码后的 surveyId 获取问卷
            Survey survey = surveyService.getSurveyById(surveyId);
            if(survey.getStatus()!=Survey.SurveyStatus.ACTIVE){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }

            //检查用户是否登录，若未登录，则返回登录页面
            //Survey.AccessLevel.PRIVATE该访问级别要求已经登录用户填写
            if(survey.getAccessLevel() == Survey.AccessLevel.PRIVATE){
                // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                // 判断是否有有效的认证信息
                if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You need to be logged in to access this survey.");
                }
                Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 user
                if (userId == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
                }
            }

            //ToDo:考虑不在这里实现，将在新方法里单独实现
            //Survey.AccessLevel.RESTRICTED 该访问级别要求特定用户填写
            if(survey.getAccessLevel() == Survey.AccessLevel.RESTRICTED){

            }
            SurveyDTO surveyDTO = surveyService.toSurveyDTO(survey);
            surveyDTO.setUserSetDuration(null);
            surveyDTO.setMaxResponses(null);
            surveyDTO.setAccessLevel(null);

            //Survey.AccessLevel.PUBLIC，若为公开问卷，无需验证，人人皆可访问
            return ResponseEntity.ok(surveyDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
