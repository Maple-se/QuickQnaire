package com.maple.quickqnairebackend.controller;

import com.maple.quickqnairebackend.dto.OptionDTO;
import com.maple.quickqnairebackend.dto.QuestionDTO;
import com.maple.quickqnairebackend.dto.SurveyDTO;
import com.maple.quickqnairebackend.dto.SurveySimpleInfoDTO;
import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.service.OptionService;
import com.maple.quickqnairebackend.service.QuestionService;
import com.maple.quickqnairebackend.service.SurveyService;
import com.maple.quickqnairebackend.service.UserService;
import com.maple.quickqnairebackend.validation.SurveyCreateGroup;
import com.maple.quickqnairebackend.validation.SurveyUpdateGroup;
import lombok.RequiredArgsConstructor;
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

/**
 * Created by zong chang on 2024/12/1 20:04
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/quickqnaire")
public class SurveyController {

    private final SurveyService surveyService;

    private final QuestionService questionService;

    private final OptionService optionService;

    private final UserService userService;


    //ToDo:后续考虑实现自定义校验错误处理器
//    @Transactional
//    @PostMapping("/create")
//    public ResponseEntity<?> createSurvey(@Validated(SurveyCreateGroup.class) @RequestBody SurveyDTO surveyCreationDTO) {
//        try {
//            // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 userId
//            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.createSurvey(surveyCreationDTO, userId);
//            for (QuestionDTO qdto : surveyCreationDTO.getQuestions()) {
//                Question createdQuestion = questionService.createQuestion(surveySimpleInfoDTO.getId(), qdto);
//                // 根据问题类型，创建选项（如果是单选或多选类型）
//                if (qdto.getType() == Question.QuestionType.SINGLE_CHOICE || qdto.getType() == Question.QuestionType.MULTIPLE_CHOICE) {
//                    for (OptionDTO odto : qdto.getOptions()) {
//                        optionService.createOption(createdQuestion.getId(), odto);
//                    }
//                }
//            }
//            return ResponseEntity.ok(surveySimpleInfoDTO);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Create Survey Error: " + e.getMessage());
//        }
//    }

    //创建问卷
    //创建问卷API测试通过
    //允许：登录用户
    @PostMapping("/create")
    public ResponseEntity<?> createSurvey(@Validated(SurveyCreateGroup.class) @RequestBody SurveyDTO surveyCreationDTO) {
            // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 userId
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.createSurvey(surveyCreationDTO, userId);
            return ResponseEntity.ok(surveySimpleInfoDTO);
    }


    //允许：问卷所有者
    @GetMapping("/preview/{encodedSurveyId}")
    public ResponseEntity<?> previewSurvey(@PathVariable String encodedSurveyId) {
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
            throw new IllegalArgumentException("Invalid survey ID format.");
        }
        // 根据解码后的 surveyId 获取问卷
        Survey survey = surveyService.getSurveyById(surveyId);
        //问卷创建者可以预览
        surveyService.validateSurveyOwnership(survey, user);
        return ResponseEntity.ok(surveyService.toSurveyDTO(survey));
    }

    //更新问卷
//    @Transactional
//    @PutMapping("/update-survey")
//    public ResponseEntity<?> updateSurveyDetail(@Validated(SurveyUpdateGroup.class) @RequestBody SurveyDTO sdto) {
//        //try {
//        // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 userId
//        User user = userService.getUserById(userId);
//
//        // 根据解码后的 surveyId 获取问卷
//        Survey survey = surveyService.getSurveyById(sdto.getSurveyId());
//
//        //问卷创建者才可以更新
//        surveyService.validateSurveyOwnership(survey, user);
//        //草稿状态才可以更新
//        surveyService.isDraftStatus(survey);
//        //增量更新（仅更新变动部分的数据（例如新增、修改），删除逻辑独立出去
//        Survey updatedSurvey = surveyService.updateSurvey(survey, sdto);
//
//        // 处理问题列表
//        if (sdto.getQuestions() != null) {
//            // 更新问题列表
//            for (QuestionDTO questionDTO : sdto.getQuestions()) {
//                Question updatedQuestion = questionService.processQuestion(updatedSurvey.getId(), questionDTO);
//                if (questionDTO.getType() == Question.QuestionType.SINGLE_CHOICE || questionDTO.getType() == Question.QuestionType.MULTIPLE_CHOICE) {
//                    for (OptionDTO odto : questionDTO.getOptions()) {
//                        optionService.processOption(updatedQuestion.getId(), odto);
//                    }
//                }
//            }
//        }
//        return ResponseEntity.ok(surveyService.surveyToSimpleInfoDTO(surveyService.getSurveyById(updatedSurvey.getId())));
//    }


    //问卷更新
    //API测试通过
    //允许：问卷所有者+问卷草稿状态
    @PutMapping("/update-survey")
    public ResponseEntity<?> updateSurveyDetail(@Validated(SurveyUpdateGroup.class) @RequestBody SurveyDTO sdto) {
        //try {
        // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 userId
        User user = userService.getUserById(userId);

        //增量更新（仅更新变动部分的数据（例如新增、修改），删除逻辑独立出去
        Survey updatedSurvey = surveyService.updateSurvey(sdto,user);
        return ResponseEntity.ok(surveyService.surveyToSimpleInfoDTO(updatedSurvey));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{encodedSurveyId}")
    public ResponseEntity<?> apiTest(@PathVariable String encodedSurveyId){
        return ResponseEntity.ok("you are admin");

    }



    //用户提交问卷，管理员审批
    //API测试通过
    //允许：问卷所有者+问卷草稿状态
    @PutMapping("/submit-for-approval/{encodedSurveyId}")
    public ResponseEntity<?> submitSurveyForApproval(@PathVariable String encodedSurveyId) {
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
            if (survey.getCreatedBy() != user) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Identity Error");
            }
            if (survey.getStatus() != Survey.SurveyStatus.DRAFT) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.submitSurveyForApproval(survey.getId());
            return ResponseEntity.ok(surveySimpleInfoDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //ToDo:问卷状态变更存在冗余代码
    //管理员批准问卷，状态变为active
    //API测试通过
    //允许：管理员+问卷PENDING_APPROVAL
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/approval-survey/{encodedSurveyId}")
    public ResponseEntity<?> approvalSurvey(@PathVariable String encodedSurveyId) {
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

            if (survey.getStatus() != Survey.SurveyStatus.PENDING_APPROVAL) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.approveSurvey(survey.getId());
            return ResponseEntity.ok(surveySimpleInfoDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //管理员拒绝问卷，状态变为草稿
    //API测试通过
    //允许：管理员+问卷PENDING_APPROVAL
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/reject-survey/{encodedSurveyId}")
    public ResponseEntity<?> rejectSurvey(@PathVariable String encodedSurveyId) {
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

            if (survey.getStatus() != Survey.SurveyStatus.PENDING_APPROVAL) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.rejectSurvey(survey.getId());
            return ResponseEntity.ok(surveySimpleInfoDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    //管理员或用户，手动关闭问卷
    //API测试通过
    //允许：管理员、问卷所有者+问卷PENDING_APPROVAL和ACTIVE
    @PutMapping("/close-survey/{encodedSurveyId}")
    public ResponseEntity<?> closeSurvey(@PathVariable String encodedSurveyId) {
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
            if (survey.getCreatedBy() != user && user.getRole() != User.Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Identity Error");
            }
            if (survey.getStatus() != Survey.SurveyStatus.PENDING_APPROVAL && survey.getStatus() != Survey.SurveyStatus.ACTIVE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.closeSurvey(survey.getId());
            return ResponseEntity.ok(surveySimpleInfoDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    //删除问卷
    //API测试通过
    //允许：管理员+问卷所有者
    @DeleteMapping("/delete-survey/{encodedSurveyId}")
    public ResponseEntity<?> deleteSurvey(@PathVariable String encodedSurveyId) {
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
            if (survey.getCreatedBy() != user && user.getRole() != User.Role.ADMIN) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Identity Error");
            }
            surveyService.deleteSurvey(survey.getId());
            return ResponseEntity.noContent().build();  // 返回 204 No Content 状态码
    }


    // 删除问题
    //API测试通过，删除逻辑暂无问题
    // ToDo:需要考虑整个Controller层架构设计问题，
    //  包括：使用 @PreAuthorize 或 @Secured 实现权限控制以及Base64解码验证逻辑，过于冗余
    //允许：问卷所有者+问卷草稿状态
    @DeleteMapping("/delete-question/{encodedSurveyId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable String encodedSurveyId, @RequestParam Long questionId) {
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
        surveyService.isDraftStatus(survey);
        if (!survey.getCreatedBy().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have permission to delete this question.");
        }
        // 删除问题
        boolean exists = questionService.IsQuestionExistInSurvey(questionId, surveyId);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question ID " + questionId + " not found in this survey");
        }
        questionService.deleteQuestion(questionId);
        // 返回成功响应
        return ResponseEntity.noContent().build();  // 返回 204 No Content 状态码
    }


    //问题选项删除逻辑
    //API测试通过
    //ToDo:有待进一步优化逻辑以及实现选项的批量删除
    //允许：问卷所有者+问卷草稿状态
    @DeleteMapping("/delete-option/{encodedSurveyId}")
    public ResponseEntity<String> deleteOption(@PathVariable String encodedSurveyId, @RequestParam Long questionId, @RequestParam Long optionId) {
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
        surveyService.isDraftStatus(survey);
        if (!survey.getCreatedBy().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have permission to delete this question.");
        }
        // 删除问题
        //ToDo:需判断问题是否存在问卷内以及选项是否存在于问题内
        boolean exists = optionService.IsOptionExistInQuestion(optionId,questionId);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Option ID " + optionId + " not found in this question");
        }
        optionService.deleteOption(optionId);
        // 返回成功响应
        return ResponseEntity.noContent().build();  // 返回 204 No Content 状态码
    }

    // 根据用户ID获取所有问卷
    //允许：问卷所有者
    @GetMapping("/surveys")
    public ResponseEntity<?> getSurveysByUser() {
        try {
            // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 user
            List<SurveySimpleInfoDTO> surveySimpleInfoDTOS = surveyService.getSurveysByUserId(userId);
            return ResponseEntity.ok(surveySimpleInfoDTOS);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Get Surveys By UserId Error");
        }
    }

    // 获取问卷信息，surveyId 通过 Base64 编码并传递
    //因在antMatchers("/quickqnaire/detail/**").permitAll() 处放开了该API，因此需要对
    // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //上下文对象进行详细的判空处理
    //问卷填写控制层
    //问卷发布，按照问卷访问控制级别处理
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
            if (survey.getStatus() != Survey.SurveyStatus.ACTIVE) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Survey Status Error");
            }

            //检查用户是否登录，若未登录，则返回登录页面
            //Survey.AccessLevel.PRIVATE该访问级别要求已经登录用户填写
            if (survey.getAccessLevel() == Survey.AccessLevel.PRIVATE) {
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
            if (survey.getAccessLevel() == Survey.AccessLevel.RESTRICTED) {

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
