package com.maple.quickqnairebackend.mapper;

import com.maple.quickqnairebackend.dto.QuestionResultDTO;
import com.maple.quickqnairebackend.dto.SurveyResultDTO;
import com.maple.quickqnairebackend.entity.*;
import com.maple.quickqnairebackend.repository.UserRepository;
import com.maple.quickqnairebackend.service.QuestionService;
import com.maple.quickqnairebackend.service.SurveyService;
import com.maple.quickqnairebackend.service.UserService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zong chang on 2024/12/21 21:54
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Mapper(componentModel = "spring")
public abstract class SurveyResultMapper {


    @Autowired
    protected SurveyService surveyService;

    @Autowired
    protected QuestionService questionService;


    // 从 SurveyResultDTO 到 SurveyResult 的映射
    @Mapping(target = "survey", expression = "java(surveyService.getSurveyById(surveyResultDTO.getSurveyId()))")
    @Mapping(target = "user",ignore = true)
    public abstract SurveyResult toEntity(SurveyResultDTO surveyResultDTO);

    // 从 QuestionResultDTO 到 QuestionResult 的映射
    @Mapping( target = "question",expression = "java(questionService.getQuestionById(questionResultDTO.getQuestionId()))") // 自动映射 questionId 为 Question 实体
    public abstract QuestionResult toEntity(QuestionResultDTO questionResultDTO);


    @AfterMapping
    public void setSurveyResultInQuestionResults(@MappingTarget SurveyResult surveyResult) {
        if (surveyResult.getQuestionResults() != null) {
            surveyResult.getQuestionResults().forEach(questionResult -> questionResult.setSurveyResult(surveyResult));
        }
    }

}
