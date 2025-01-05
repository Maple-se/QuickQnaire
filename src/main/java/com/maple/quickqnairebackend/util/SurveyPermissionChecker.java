package com.maple.quickqnairebackend.util;

/**
 * Created by zong chang on 2024/12/24 0:23
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.service.SurveyService;
import com.maple.quickqnairebackend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("surveyPermission")
public class SurveyPermissionChecker{

    private final UserService userService;
    private final SurveyService surveyService;
    private Survey surveyToCheck;
    private User authUser;

    public SurveyPermissionChecker(UserService userService, SurveyService surveyService) {
        this.userService = userService;
        this.surveyService = surveyService;
    }

    // 在需要时初始化
    public void init(Authentication authentication) {
        String encodedSurveyId = SecurityUtil.getSurveyDetail(authentication); // 获取编码后的 Survey ID
        Long authUserId = SecurityUtil.getUserId(authentication);
        System.out.println(encodedSurveyId);
        this.authUser = null;
        this.surveyToCheck = null;
        if (encodedSurveyId != null) {
            this.surveyToCheck = surveyService.getSurveyById(surveyService.getDecodedSurveyId(encodedSurveyId));
        }
        if(authUserId != null){
            this.authUser = userService.getUserById(authUserId);
        }
    }


    public boolean authUser(){
        init(SecurityUtil.getAuthentication());
        return this.authUser != null;
    }

    public boolean owner(){
       init(SecurityUtil.getAuthentication());
       return this.surveyToCheck.getCreatedBy().equals(this.authUser);
    }

    public boolean admin(){
        init(SecurityUtil.getAuthentication());
        return this.authUser.getRole().equals(User.Role.ADMIN);
    }

    public boolean draft(){
        init(SecurityUtil.getAuthentication());
        return this.surveyToCheck.getStatus().equals(Survey.SurveyStatus.DRAFT);
    }

    public boolean active(){
        init(SecurityUtil.getAuthentication());
        return this.surveyToCheck.getStatus().equals(Survey.SurveyStatus.ACTIVE);
    }

    public boolean approval(){
        init(SecurityUtil.getAuthentication());
        return this.surveyToCheck.getStatus().equals(Survey.SurveyStatus.PENDING_APPROVAL);
    }

    public boolean ownerAndDraft(){
        return owner() && draft();
    }

    public boolean ownerAndApproval(){
        return owner() && approval();
    }

    public boolean ownerAndActive(){
        return owner() && active();
    }

    public boolean adminAndApproval(){
        return admin() && approval();
    }

    public boolean adminAndActive(){
        return admin() && active();
    }

}
