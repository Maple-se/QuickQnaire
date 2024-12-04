package com.maple.quickqnairebackend.controller;

import com.maple.quickqnairebackend.dto.*;
import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.entity.QuestionOption;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.service.OptionService;
import com.maple.quickqnairebackend.service.QuestionService;
import com.maple.quickqnairebackend.service.SurveyService;
import com.maple.quickqnairebackend.service.UserService;
import com.maple.quickqnairebackend.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    private AuthenticationUtil authenticationUtil;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private UserService userService;

    //创建问卷
    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createSurvey(@RequestHeader("Authorization") String authorization,@Valid @RequestBody SurveyCreationDTO surveyCreationDTO){
        try{
            Long userId = authenticationUtil.authenticateAndGetUserId(authorization);
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.createSurvey(surveyCreationDTO,userId);
            for (QuestionCreationDTO qdto: surveyCreationDTO.getQuestions()) {
                Question createdQuestion = questionService.createQuestion(surveySimpleInfoDTO.getId(),qdto);
                // 根据问题类型，创建选项（如果是单选或多选类型）
                if (qdto.getQuestionType() == Question.QuestionType.SINGLE_CHOICE || qdto.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE) {
                    for (QuestionOptionCreationDTO odto: qdto.getOptions()) {
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

    //更新问卷
    //ToDo:将创建与更新抽象出基类，需调整DTO部分的设计
    @Transactional
    @PutMapping("/update-survey")
    public ResponseEntity<?> updateSurveyDetail(@RequestHeader(value = "Authorization") String authorization,@Valid @RequestBody SurveyUpdateDTO sdto){
        try {
            Long userId = authenticationUtil.authenticateAndGetUserId(authorization);
            User user = userService.getUserById(userId);
            // 根据解码后的 surveyId 获取问卷
            Survey survey = surveyService.getSurveyById(sdto.getSurveyId());
            if(survey.getCreatedBy()!=user){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Identity Error");
            }
            if(survey.getStatus() != Survey.SurveyStatus.DRAFT){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.updateSurvey(survey,sdto);

            // 更新问题列表
            for (SurveyUpdateDTO.QuestionUpdateDTO questionDTO : sdto.getQuestions()) {
                if (questionDTO.getId() != null) {
                    // 如果问题ID存在，更新现有问题
                    Question question = questionService.getQuestionById(questionDTO.getId());
                    Question updatedQuestion = questionService.updateQuestion(question,questionDTO);
                    // 更新选项
                    for (SurveyUpdateDTO.QuestionUpdateDTO.OptionUpdateDTO optionDTO : questionDTO.getOptions()) {
                        if (optionDTO.getId() != null) {
                            // 如果选项ID存在，更新现有选项
                            QuestionOption option = optionService.getOptionById(optionDTO.getId());
                            QuestionOption updatedOption = optionService.updateOption(option,optionDTO);
                        } else {
                            // 新增选项
                           // optionService.createOption(updatedQuestion.getId(),optionDTO);
//                            QuestionOption newOption = new QuestionOption();
//                            newOption.setOptionText(optionDTO.getOptionText());
//                            question.addOption(newOption);
                        }
                    }
                } else {
                    // 新增问题
//                    Question newQuestion = new Question();
//                    newQuestion.setQuestionText(questionDTO.getQuestionText());
//                    newQuestion.setQuestionType(questionDTO.getQuestionType());
//                    newQuestion.setRequired(questionDTO.getRequired());
//
//                    // 新增选项
//                    for (SurveyUpdateDTO.QuestionDTO.QuestionOptionDTO optionDTO : questionDTO.getOptions()) {
//                        QuestionOption newOption = new QuestionOption();
//                        newOption.setOptionText(optionDTO.getOptionText());
//                        newQuestion.addOption(newOption);
//                    }
//
//                    survey.addQuestion(newQuestion);
                }
            }
            return ResponseEntity.ok(surveySimpleInfoDTO);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }


    //用户提交问卷，管理员审批
    @PutMapping("/submit-for-approval/{encodedSurveyId}")
    public ResponseEntity<?> submitSurveyForApproval(@RequestHeader(value = "Authorization") String authorization, @PathVariable String encodedSurveyId){
       try {
           Long userId = authenticationUtil.authenticateAndGetUserId(authorization);
           User user = userService.getUserById(userId);
           // 解码 Base64 编码的 surveyId
           String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
           Long surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
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


    //管理员批准问卷，状态变为active
    @PutMapping("/approval-survey/{encodedSurveyId}")
    public ResponseEntity<?> approvalSurvey(@RequestHeader(value = "Authorization") String authorization, @PathVariable String encodedSurveyId){
        try {
            Long userId = authenticationUtil.authenticateAndGetUserId(authorization);
            User adminUser = userService.getUserById(userId);
            if(adminUser.getRole() != User.Role.ADMIN){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Identity Error");
            }
            // 解码 Base64 编码的 surveyId
            String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
            Long surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
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
    @PutMapping("/reject-survey/{encodedSurveyId}")
    public ResponseEntity<?> rejectSurvey(@RequestHeader(value = "Authorization") String authorization, @PathVariable String encodedSurveyId){
        try {
            Long userId = authenticationUtil.authenticateAndGetUserId(authorization);
            User adminUser = userService.getUserById(userId);
            if(adminUser.getRole() != User.Role.ADMIN){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Identity Error");
            }
            // 解码 Base64 编码的 surveyId
            String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
            Long surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
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
    @PutMapping("/close-survey/{encodedSurveyId}")
    public ResponseEntity<?> closeSurvey(@RequestHeader(value = "Authorization") String authorization, @PathVariable String encodedSurveyId){
        try {
            Long userId = authenticationUtil.authenticateAndGetUserId(authorization);
            User user = userService.getUserById(userId);

            // 解码 Base64 编码的 surveyId
            String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
            Long surveyId = Long.parseLong(decodedId); // 转换为 Long 类型
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


    // 根据用户ID获取所有问卷
    @GetMapping("/surveys")
    public ResponseEntity<?> getSurveysByUserId(@RequestHeader("Authorization") String authorization) {
        try {
            Long userId = authenticationUtil.authenticateAndGetUserId(authorization);
            List<SurveySimpleInfoDTO> surveyDTOs = surveyService.getSurveysByUserId(userId);
            return ResponseEntity.ok(surveyDTOs);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Get Surveys By UserId Error");
        }
    }

    // 获取问卷信息，surveyId 通过 Base64 编码并传递
    @Transactional
    @GetMapping("/detail/{encodedSurveyId}")
    public ResponseEntity<?> getSurveyById(@RequestHeader(value = "Authorization", required = false) String authorization,@PathVariable String encodedSurveyId) {
        try {
            // 解码 Base64 编码的 surveyId
            String decodedId = new String(Base64.getUrlDecoder().decode(encodedSurveyId));
            Long surveyId = Long.parseLong(decodedId); // 转换为 Long 类型

            // 根据解码后的 surveyId 获取问卷
            Survey survey = surveyService.getSurveyById(surveyId);
            if(survey.getStatus()!=Survey.SurveyStatus.ACTIVE){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }

            //检查用户是否登录，若未登录，则返回登录页面
            //Survey.AccessLevel.PRIVATE该访问级别要求已经登录用户填写
            if(survey.getAccessLevel() == Survey.AccessLevel.PRIVATE){
                Long userId = authenticationUtil.authenticateAndGetUserId(authorization);
            }

            //ToDo:暂时不实现
            //Survey.AccessLevel.RESTRICTED 该访问级别要求特定用户填写
            if(survey.getAccessLevel() == Survey.AccessLevel.RESTRICTED){

            }

            // 构造 SurveyAnswerDTO
            SurveyDetailDTO surveyAnswerDTO = new SurveyDetailDTO();
            surveyAnswerDTO.setSurveyId(survey.getId());
            surveyAnswerDTO.setTitle(survey.getTitle());
            surveyAnswerDTO.setDescription(survey.getDescription());

            // 设置问题和选项
            List<SurveyDetailDTO.QuestionDetailDTO> questionDTOList = survey.getQuestions().stream().map(question -> {
                SurveyDetailDTO.QuestionDetailDTO questionDTO = new SurveyDetailDTO.QuestionDetailDTO();
                questionDTO.setQuestionId(question.getId());
                questionDTO.setQuestionContent(question.getQuestionContent());
                questionDTO.setQuestionType(question.getType());

                // 设置选项
                List<SurveyDetailDTO.QuestionDetailDTO.OptionDetailDTO> options = question.getOptions().stream().map(option -> {
                    SurveyDetailDTO.QuestionDetailDTO.OptionDetailDTO optionDTO = new SurveyDetailDTO.QuestionDetailDTO.OptionDetailDTO();
                    optionDTO.setOptionId(option.getId());
                    optionDTO.setOptionContent(option.getOptionContent());
                    return optionDTO;
                }).collect(Collectors.toList());

                questionDTO.setOptions(options);

                return questionDTO;
            }).collect(Collectors.toList());

            surveyAnswerDTO.setQuestions(questionDTOList);
            //Survey.AccessLevel.PUBLIC，若为公开问卷，无需验证，人人皆可访问
            return ResponseEntity.ok(surveyAnswerDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
