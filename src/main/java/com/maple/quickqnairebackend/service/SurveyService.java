package com.maple.quickqnairebackend.service;

import com.maple.quickqnairebackend.dto.SurveyDTO;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public SurveyDTO createSurvey(Survey survey, Long userId) {
        User user = userService.getUserById(userId);
        survey.setDuration(defaultSurveyDuration);
        survey.setCreatedBy(user);  // 设置创建者
        survey.create(); // 设置其他必要字段

        // 保存 Survey 并返回状态字段 DTO
        Survey savedSurvey = surveyRepository.save(survey);

        if (savedSurvey != null) {
            return surveyToDTO(survey);
        } else {
            return new SurveyDTO(null, null,null, null, "Survey_Created_Failed");
        }
    }

    // 获取所有问卷
    public List<Survey> getAllSurveys() {
        return surveyRepository.findAll();
    }

    // 获取单个问卷详情
    public Survey getSurveyById(Long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
    }

    // 更新问卷信息
    @Transactional
    public SurveyDTO updateSurvey(Long id, Survey updatedSurvey) {
        Survey existingSurvey = surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));

        //只允许在草稿draft状态下进行问卷更新
        if(existingSurvey.getStatus()!= Survey.SurveyStatus.DRAFT){
            throw new IllegalArgumentException("Survey status error");
        }
        // 更新基本信息
        existingSurvey.setTitle(updatedSurvey.getTitle());
        existingSurvey.setDescription(updatedSurvey.getDescription());
        existingSurvey.setMaxResponses(updatedSurvey.getMaxResponses());
        existingSurvey.setUserSetDuration(updatedSurvey.getUserSetDuration());
        existingSurvey.setAccessLevel(updatedSurvey.getAccessLevel());

        return surveyToDTO(surveyRepository.save(existingSurvey));
    }


    // 删除问卷
    @Transactional
    public void deleteSurvey(Long id) {
        if (surveyRepository.existsById(id)) {
            surveyRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Survey_Found_Error");
        }
    }


    // 如果你需要额外的处理或数据转换，可以在服务层实现
    public List<SurveyDTO> getSurveysByUserId(Long userId) {
        User user = userService.getUserById(userId);  // 假设你有一个用户服务层来根据ID获取用户
        List<Survey> surveys = surveyRepository.findByCreatedBy(user);
        // 将 Survey 转换为 DTO
        return surveysToDTO(surveys);
    }

    // 根据状态获取问卷
    public List<SurveyDTO> getSurveysByStatus(Survey.SurveyStatus status) {
        return surveysToDTO(surveyRepository.findByStatus(status));
    }

    // 根据问卷ID判断是否已关闭
    public boolean isSurveyClosed(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
        return survey.getStatus() == Survey.SurveyStatus.CLOSED;
    }

    // 用户提交问卷，管理员进行审核
    @Transactional
    public void submitSurveyForApproval(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
        survey.submit();
        surveyRepository.save(survey);
    }

    // 管理员批准问卷
    @Transactional
    public void approveSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
        survey.approve();
        surveyRepository.save(survey);
    }

    // 管理员拒绝问卷
    @Transactional
    public void rejectSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
        survey.reject();
        surveyRepository.save(survey);
    }

    // 关闭问卷
    @Transactional
    public void closeSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
        survey.close();
        surveyRepository.save(survey);
    }


    //工具方法
    //Surveys到SurveyDTO的转换
    private List<SurveyDTO> surveysToDTO(List<Survey> surveys) {
        return surveys
                .stream()
                .map(this::surveyToDTO)
                .collect(Collectors.toList());
    }

    // Survey到SurveyDTO的转换
    private SurveyDTO surveyToDTO(Survey survey) {
        return new SurveyDTO(survey.getId(),survey.getTitle() ,survey.getDescription(),survey.getStatus(),"Survey_Created_OK");
    }
}

