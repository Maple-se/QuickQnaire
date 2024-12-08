package com.maple.quickqnairebackend.service;

import com.maple.quickqnairebackend.dto.*;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.repository.SurveyRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
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
    public SurveySimpleInfoDTO createSurvey(SurveyDTO creationDTO, Long userId) {
        User user = userService.getUserById(userId);
        Survey survey = toEntity(creationDTO);
        survey.setDuration(defaultSurveyDuration);
        survey.setCreatedBy(user);  // 设置创建者
        survey.create(); // 设置其他必要字段
        if(user.getRole().equals(User.Role.ADMIN)){
            survey.approve();
        }
        // 保存 Survey 并返回状态字段 DTO
         return  surveyToSimpleInfoDTO(surveyRepository.save(survey));
    }


    private Survey toEntity(SurveyDTO dto) {
        Survey survey = new Survey();
        survey.setTitle(dto.getTitle());
        survey.setDescription(dto.getDescription());
        survey.setAccessLevel(dto.getAccessLevel());
        survey.setUserSetDuration(dto.getUserSetDuration());
        survey.setMaxResponses(dto.getMaxResponses());
        return survey;
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
    public Survey updateSurvey(Survey survey, SurveyDTO updatedSurvey) {
        // 更新基本信息
        if (StringUtils.isNotBlank(updatedSurvey.getTitle())) survey.setTitle(updatedSurvey.getTitle());
        if (StringUtils.isNotBlank(updatedSurvey.getDescription())) survey.setDescription(updatedSurvey.getDescription());
        if (updatedSurvey.getAccessLevel() != null) survey.setAccessLevel(updatedSurvey.getAccessLevel());
        if (updatedSurvey.getUserSetDuration() != null) survey.setUserSetDuration(updatedSurvey.getUserSetDuration());
        if (updatedSurvey.getMaxResponses() != null) survey.setMaxResponses(updatedSurvey.getMaxResponses());

       return surveyRepository.save(survey);
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
    public List<SurveySimpleInfoDTO> getSurveysByUserId(Long userId) {
        User user = userService.getUserById(userId);  // 假设你有一个用户服务层来根据ID获取用户
        List<Survey> surveys = surveyRepository.findByCreatedBy(user);
        // 将 Survey 转换为 DTO
        return surveysToSimpleInfoDTO(surveys);
    }

    // 根据状态获取问卷
    public List<SurveySimpleInfoDTO> getSurveysByStatus(Survey.SurveyStatus status) {
        return surveysToSimpleInfoDTO(surveyRepository.findByStatus(status));
    }


    // 检查问卷是否应当结束
    //ToDo:待考虑
    public boolean isSurveyshouldClose(Long surveyId) {
        if(isSurveyClosed(surveyId)){
            return false;
        }
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey not found"));

        boolean shouldClose = false;

        // 1. 达到最大回答数
        if (survey.getMaxResponses() != null && survey.getResponsesReceived() >= survey.getMaxResponses()) {
            shouldClose = true;  // 满足最大回答数条件
        }

        // 2. 达到用户设置的持续时间
        if (survey.getUserSetDuration() != null && isUserSetDurationExpired(survey)) {
            shouldClose = true;  // 满足用户设置持续时间条件
        }

        // 3. 达到系统默认的持续时间
        if (survey.getDuration() != null && survey.getActiveStartDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(survey.getActiveStartDate());
            calendar.add(Calendar.HOUR, survey.getDuration());
            Date systemDefaultEnd = calendar.getTime();
            if (new Date().after(systemDefaultEnd)) {
                //survey.close();  // 达到系统默认的持续时间，关闭问卷
                return true;
            }
        }

        // 如果满足任何一个条件，提示用户是否关闭问卷
        //promptUserToCloseSurvey(survey);
        return shouldClose;
    }

    // 判断用户设置的持续时间是否已过期
    private boolean isUserSetDurationExpired(Survey survey) {
        if (survey.getUserSetDuration() == null || survey.getStatus() != Survey.SurveyStatus.ACTIVE || survey.getActiveStartDate() == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(survey.getActiveStartDate());  // 从`ACTIVE`开始时间计算
        calendar.add(Calendar.HOUR, survey.getUserSetDuration());  // 增加用户设置的小时数
        Date userSetEnd = calendar.getTime();

        return new Date().after(userSetEnd);  // 如果当前时间已超过结束时间，则返回 true
    }

    private void promptUserToCloseSurvey(Survey survey) {
        // 这里返回给前端提示，用户可以选择关闭问卷
        // 可以考虑返回一个提示消息，等待用户确认是否关闭
        // 例如： "问卷已经满足条件，是否关闭问卷？"
    }

    // 根据问卷ID判断是否已关闭
    public boolean isSurveyClosed(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
        return survey.getStatus() == Survey.SurveyStatus.CLOSED;
    }

    // 用户提交问卷，管理员进行审核
    @Transactional
    public SurveySimpleInfoDTO submitSurveyForApproval(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
        survey.submit();
       return surveyToSimpleInfoDTO(surveyRepository.save(survey)) ;
    }

    // 管理员批准问卷
    @Transactional
    public SurveySimpleInfoDTO approveSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
        survey.approve();
      return surveyToSimpleInfoDTO(surveyRepository.save(survey));
    }

    // 管理员拒绝问卷
    @Transactional
    public SurveySimpleInfoDTO rejectSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
        survey.reject();
        return surveyToSimpleInfoDTO(surveyRepository.save(survey));
    }

    // 关闭问卷
    @Transactional
    public SurveySimpleInfoDTO closeSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("Survey_Found_Error"));
        survey.close();
        return surveyToSimpleInfoDTO(surveyRepository.save(survey));
    }


    //工具方法

    // 验证问卷创建者
    public void validateSurveyOwnership(Survey survey, User user) {
        if (!survey.getCreatedBy().equals(user)) {
            throw new IllegalArgumentException("User Identity Error");
        }
    }

    // 验证问卷状态
    public void isDraftStatus(Survey survey) {
        if (survey.getStatus() != Survey.SurveyStatus.DRAFT) {
            throw new IllegalArgumentException("Survey Status Error");
        }
    }

    //ToDo:计划使用MapStruct重构
    public SurveyDTO toSurveyDTO(Survey survey){

        SurveyDTO surveyDTO = new SurveyDTO();
        surveyDTO.setSurveyId(survey.getId());
        surveyDTO.setTitle(survey.getTitle());
        surveyDTO.setDescription(survey.getDescription());
        //
        surveyDTO.setAccessLevel(survey.getAccessLevel());
        surveyDTO.setUserSetDuration(survey.getUserSetDuration());
        surveyDTO.setMaxResponses(survey.getMaxResponses());

        // 设置问题和选项
        List<QuestionDTO> questionDTOList = survey.getQuestions().stream().map(question -> {
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setQuestionId(question.getId());
            questionDTO.setQuestionContent(question.getQuestionContent());
            questionDTO.setQuestionType(question.getType());
            questionDTO.setRequired(question.getRequired());

            // 设置选项
           // if(question.getOptions()!=null) {
                List<OptionDTO> options = question.getOptions().stream().map(option -> {
                    OptionDTO optionDTO = new OptionDTO();
                    optionDTO.setOptionId(option.getId());
                    optionDTO.setOptionContent(option.getOptionContent());
                    return optionDTO;
                }).collect(Collectors.toList());

                questionDTO.setOptions(options);
           // }

            return questionDTO;
        }).collect(Collectors.toList());

      surveyDTO.setQuestions(questionDTOList);
      return surveyDTO;
    }

    //Surveys到SurveyDTO的转换
    private List<SurveySimpleInfoDTO> surveysToSimpleInfoDTO(List<Survey> surveys) {
        return surveys
                .stream()
                .map(this::surveyToSimpleInfoDTO)
                .collect(Collectors.toList());
    }

    // Survey到SurveyDTO的转换
    private SurveySimpleInfoDTO surveyToSimpleInfoDTO(Survey survey) {
        return new SurveySimpleInfoDTO(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getStatus(),
                survey.getAccessLevel(),
                survey.getUpdatedAt()
        );
    }
}

