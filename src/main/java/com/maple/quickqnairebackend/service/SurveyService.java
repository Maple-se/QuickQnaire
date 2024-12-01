package com.maple.quickqnairebackend.service;

import com.maple.quickqnairebackend.dto.SurveyDTO;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zong chang on 2024/12/1 18:38
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private UserService userService;  // 通过 UserService 获取用户信息

    @Value("${survey.default.duration}")
    private int defaultSurveyDuration;  // 默认持续时间，单位：小时


    // 创建新问卷
    public Survey createSurvey(Survey survey, Long userId) {
            User user = userService.getUserById(userId);
            survey.setDuration(defaultSurveyDuration);
            survey.setCreatedBy(user);  // 设置创建者
            //status
            survey.creat();
            return surveyRepository.save(survey);
    }

    // 获取所有问卷
    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }

    // 获取单个问卷详情
    public Survey getSurveyById(Long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));
    }

    // 更新问卷信息

    public Survey updateSurvey(Long id, Survey updatedSurvey) {
        Survey existingSurvey = surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        //只允许在草稿draft状态下进行问卷更新
        if(existingSurvey.getStatus()!= Survey.SurveyStatus.DRAFT){
            throw new IllegalArgumentException("Survey status error");
        }
        // 更新基本信息
        existingSurvey.setTitle(updatedSurvey.getTitle());
        existingSurvey.setDescription(updatedSurvey.getDescription());
        existingSurvey.setMaxResponses(updatedSurvey.getMaxResponses());
        existingSurvey.setUserSetDuration(updatedSurvey.getUserSetDuration());
        //existingSurvey.setDuration(updatedSurvey.getDuration());
        existingSurvey.setAccessLevel(updatedSurvey.getAccessLevel());

        return surveyRepository.save(existingSurvey);
    }
    // 用户提交问卷，管理员进行审核
    public void submitSurveyForApproval(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));
        survey.submit();
        surveyRepository.save(survey);
    }


    // 删除问卷
    public void deleteSurvey(Long id) {
        if (surveyRepository.existsById(id)) {
            surveyRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Survey not found");
        }
    }

    // 根据用户查询该用户的所有问卷
//    public List<Survey> getSurveysByUser(User user) {
//        return surveyRepository.findByCreatedBy(user);  // 使用上面创建的查询方法
//    }

    // 如果你需要额外的处理或数据转换，可以在服务层实现
    public List<SurveyDTO> getSurveysByUserId(Long userId) {
        User user = userService.getUserById(userId);  // 假设你有一个用户服务层来根据ID获取用户
        List<Survey> surveys = surveyRepository.findByCreatedBy(user);

        // 将 Survey 转换为 DTO（如果需要）
        List<SurveyDTO> surveyDTOs = surveys.stream()
                .map(this::convertToSurveyDTO)
                .collect(Collectors.toList());
        return surveyDTOs;
    }

    // Survey到SurveyDTO的转换
    private SurveyDTO convertToSurveyDTO(Survey survey) {
        // 转换逻辑
        return new SurveyDTO(survey.getId(), survey.getTitle(), survey.getStatus());
    }
    // 根据状态获取问卷
    public List<Survey> getSurveysByStatus(Survey.SurveyStatus status) {
        return surveyRepository.findByStatus(status);
    }

    // 根据问卷ID判断是否已关闭
    public boolean isSurveyClosed(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));
        return survey.getStatus() == Survey.SurveyStatus.CLOSED;
    }

    // 管理员批准问卷
    public void approveSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));
        survey.approve();
        surveyRepository.save(survey);
    }

    // 管理员拒绝问卷
    public void rejectSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));
        survey.reject();
        surveyRepository.save(survey);
    }

    // 关闭问卷
    public void closeSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));
        survey.close();
        surveyRepository.save(survey);
    }
}

