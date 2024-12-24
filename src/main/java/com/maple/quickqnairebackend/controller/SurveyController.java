package com.maple.quickqnairebackend.controller;

import com.maple.quickqnairebackend.dto.SurveyDTO;
import com.maple.quickqnairebackend.dto.SurveySimpleInfoDTO;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.service.OptionService;
import com.maple.quickqnairebackend.service.QuestionService;
import com.maple.quickqnairebackend.service.SurveyService;
import com.maple.quickqnairebackend.validation.SurveyCreateGroup;
import com.maple.quickqnairebackend.validation.SurveyUpdateGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
@RequiredArgsConstructor
@RequestMapping("/quickqnaire")
public class SurveyController {

    private final SurveyService surveyService;

    private final QuestionService questionService;

    private final OptionService optionService;


    //ToDo:后续考虑实现自定义校验错误处理器
    //创建问卷
    //API测试通过
    //resource: survey permission:User.Auth
    @PreAuthorize("@surveyPermission.authUser()")
    @PostMapping("/create")
    public ResponseEntity<?> createSurvey(@Validated(SurveyCreateGroup.class) @RequestBody SurveyDTO surveyCreationDTO) {
            SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.createSurvey(surveyCreationDTO);
            return ResponseEntity.ok(surveySimpleInfoDTO);
    }


    //问卷预览
    //API测试通过
    //resource: survey permission:User.OWNER and SurveyStatus.ANY
    @PreAuthorize("@surveyPermission.owner()")
    @GetMapping("/preview/{encodedSurveyId}")
    public ResponseEntity<?> previewSurvey(@PathVariable String encodedSurveyId) {
        Long surveyId = surveyService.getDecodedSurveyId(encodedSurveyId);
        Survey survey = surveyService.getSurveyById(surveyId);
        SurveyDTO surveyDTO = surveyService.toSurveyDTO(survey);
        return ResponseEntity.ok(surveyDTO);
    }

    //问卷更新
    //API测试通过
    //resource: survey permission:User.OWNER and SurveyStatus.DRAFT
    @PreAuthorize("@surveyPermission.ownerAndDraft()")
    @PutMapping("/update-survey/{encodedSurveyId}")
    public ResponseEntity<?> updateSurveyDetail(@Validated(SurveyUpdateGroup.class) @RequestBody SurveyDTO sdto,@PathVariable String encodedSurveyId) {
        //增量更新（仅更新变动部分的数据（例如新增、修改），删除逻辑独立出去
        Survey updatedSurvey = surveyService.updateSurvey(sdto);
        return ResponseEntity.ok(surveyService.surveyToSimpleInfoDTO(updatedSurvey));
    }

    //用户提交问卷，管理员审批
    //API测试通过
    //resource: survey permission:User.OWNER and SurveyStatus.DRAFT
    @PreAuthorize("@surveyPermission.ownerAndDraft()")
    @PutMapping("/submit-for-approval/{encodedSurveyId}")
    public ResponseEntity<?> submitSurveyForApproval(@PathVariable String encodedSurveyId) {
       Long surveyId = surveyService.getDecodedSurveyId(encodedSurveyId);
       SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.submitSurveyForApproval(surveyId);
       return ResponseEntity.ok(surveySimpleInfoDTO);
    }

    //管理员批准问卷，状态变为active
    //API测试通过
    //resource: survey permission:User.ADMIN and SurveyStatus.PENDING_APPROVAL
    @PreAuthorize("@surveyPermission.adminAndApproval()")
    @PutMapping("/approval-survey/{encodedSurveyId}")
    public ResponseEntity<?> approvalSurvey(@PathVariable String encodedSurveyId) {
        Long surveyId = surveyService.getDecodedSurveyId(encodedSurveyId);
        SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.approveSurvey(surveyId);
        return ResponseEntity.ok(surveySimpleInfoDTO);
    }

    //管理员拒绝问卷，状态变为草稿
    //API测试通过
    //允许：管理员+问卷状态PENDING_APPROVAL
    //resource: survey permission:User.ADMIN and SurveyStatus.PENDING_APPROVAL
    @PreAuthorize("@surveyPermission.adminAndApproval()")
    @PutMapping("/reject-survey/{encodedSurveyId}")
    public ResponseEntity<?> rejectSurvey(@PathVariable String encodedSurveyId) {
        Long surveyId = surveyService.getDecodedSurveyId(encodedSurveyId);
        SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.rejectSurvey(surveyId);
        return ResponseEntity.ok(surveySimpleInfoDTO);
    }

    //管理员或用户，关闭问卷
    //API测试通过
    //允许：管理员/问卷所有者+问卷状态PENDING_APPROVAL/ACTIVE
    //resource: survey permission:User.ADMIN/OWNER and SurveyStatus.PENDING_APPROVAL/ACTIVE
    @PreAuthorize(
            "@surveyPermission.adminAndApproval() " +
            "or @surveyPermission.adminAndActive()" +
            "or @surveyPermission.ownerAndApproval()" +
            "or @surveyPermission.ownerAndActive()"
    )
    @PutMapping("/close-survey/{encodedSurveyId}")
    public ResponseEntity<?> closeSurvey(@PathVariable String encodedSurveyId) {
        Long surveyId = surveyService.getDecodedSurveyId(encodedSurveyId);
        SurveySimpleInfoDTO surveySimpleInfoDTO = surveyService.closeSurvey(surveyId);
        return ResponseEntity.ok(surveySimpleInfoDTO);
    }

    //删除问卷
    //API测试通过
    //resource: survey permission:User.ADMIN/OWNER and SurveyStatus.ANY
    @PreAuthorize(
            "@surveyPermission.admin()" +
            "or @surveyPermission.owner()"
    )
    @DeleteMapping("/delete-survey/{encodedSurveyId}")
    public ResponseEntity<?> deleteSurvey(@PathVariable String encodedSurveyId) {
        Long surveyId = surveyService.getDecodedSurveyId(encodedSurveyId);
        surveyService.deleteSurvey(surveyId);
        return ResponseEntity.noContent().build();  // 返回 204 No Content 状态码
    }

    // 删除问题
    //API测试通过，删除逻辑暂无问题
    //resource: survey permission:User.OWNER and SurveyStatus.DRAFT
    @PreAuthorize("@surveyPermission.ownerAndDraft()")
    @DeleteMapping("/delete-question/{encodedSurveyId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable String encodedSurveyId, @RequestParam Long questionId) {
        Long surveyId = surveyService.getDecodedSurveyId(encodedSurveyId);
        // 删除问题
        boolean exists = questionService.IsQuestionExistInSurvey(questionId, surveyId);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Question ID " + questionId + " not found in this survey");
        }
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();  // 返回 204 No Content 状态码
    }


    //问题选项删除逻辑
    //API测试通过
    //ToDo:有待进一步优化逻辑以及实现选项的批量删除
    //resource: survey permission:User.OWNER and SurveyStatus.DRAFT
    @PreAuthorize("@surveyPermission.ownerAndDraft()")
    @DeleteMapping("/delete-option/{encodedSurveyId}")
    public ResponseEntity<String> deleteOption(@PathVariable String encodedSurveyId, @RequestParam Long questionId, @RequestParam Long optionId) {
        Long surveyId = surveyService.getDecodedSurveyId(encodedSurveyId);//ToDo
        // 删除问题
        //ToDo:需判断问题是否存在问卷内以及选项是否存在于问题内
        boolean exists = optionService.IsOptionExistInQuestion(optionId,questionId);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Option ID " + optionId + " not found in this question");
        }
        optionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();  // 返回 204 No Content 状态码
    }

    // 根据用户ID获取所有问卷
    //ToDo:需进一步实现分页查询以及按照问卷状态查询
    //resource: survey permission:User.OWNER
    @PreAuthorize("@surveyPermission.authUser()")
    @GetMapping("/surveys")
    public ResponseEntity<?> getSurveysByUser() {
       List<SurveySimpleInfoDTO> surveySimpleInfoDTOS = surveyService.getSurveysByUserId();
       return ResponseEntity.ok(surveySimpleInfoDTOS);
    }

    //问卷发布
    //resource: survey permission:User.Any and SurveyStatus.ACTIVE
    //ToDo:目前游客无法浏览问卷，API测试暂未通过
    @PreAuthorize("@surveyPermission.active()")
    @GetMapping("/detail/{encodedSurveyId}")
    public ResponseEntity<?> getSurveyById(@PathVariable String encodedSurveyId) {
        Long surveyId = surveyService.getDecodedSurveyId(encodedSurveyId);
        Survey survey = surveyService.getSurveyById(surveyId);
//ToDo:在问卷结果提交中实现该逻辑，问卷详情只要是Active用户和游客均可以访问
            //检查用户是否登录，若未登录，则返回登录页面
            //Survey.AccessLevel.PRIVATE该访问级别要求已经登录用户填写
//            if (survey.getAccessLevel() == Survey.AccessLevel.PRIVATE) {
//                // 通过 SecurityContext 获取用户信息，而不需要再次从请求头中获取
//                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//                // 判断是否有有效的认证信息
//                if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
//                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You need to be logged in to access this survey.");
//                }
//                Long userId = Long.parseLong(authentication.getName());  // 从 authentication 中提取 user
//                if (userId == null) {
//                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
//                }
//            }
//            //Survey.AccessLevel.RESTRICTED 该访问级别要求特定用户填写
//            if (survey.getAccessLevel() == Survey.AccessLevel.RESTRICTED) {
//
//            }
            SurveyDTO surveyDTO = surveyService.toSurveyDTO(survey);
            surveyDTO.setUserSetDuration(null);
            surveyDTO.setMaxResponses(null);
            surveyDTO.setAccessLevel(null);
            return ResponseEntity.ok(surveyDTO);
    }
}
