package com.maple.quickqnairebackend.mapper;

/**
 * Created by zong chang on 2024/12/9 21:27
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.dto.OptionDTO;
import com.maple.quickqnairebackend.dto.QuestionDTO;
import com.maple.quickqnairebackend.dto.SurveyDTO;
import com.maple.quickqnairebackend.dto.SurveySimpleInfoDTO;
import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.entity.QuestionOption;
import com.maple.quickqnairebackend.entity.Survey;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SurveyMapper {

    // 将 Survey 转换为 SurveyDTO
    @Mapping(target = "surveyId", source = "id")
    SurveyDTO toSurveyDTO(Survey survey);

    // 将 Question 转换为 QuestionDTO
    @Mapping(target = "questionId", source = "id")
    QuestionDTO toQuestionDTO(Question question);

    // 将 Option 转换为 OptionDTO
    @Mapping(target = "optionId", source = "id")
    OptionDTO toOptionDTO(QuestionOption option);

    // 转换一个 Survey 列表为 SurveySimpleInfoDTO 列表
    List<SurveySimpleInfoDTO> surveysToSimpleInfoDTO(List<Survey> surveys);

    // 转换单个 Survey 为 SurveySimpleInfoDTO
    SurveySimpleInfoDTO surveyToSimpleInfoDTO(Survey survey);


    //测试
    //转换DTO为Entity，创建问卷

    @Mapping(target = "id", source = "surveyId")
    Survey toSurvey(SurveyDTO surveyDTO);

    @Mapping(target = "id", source = "questionId")
    Question toQuestion(QuestionDTO questionDTO);

    @Mapping(target = "id", source = "optionId")
    QuestionOption toOption(OptionDTO optionDTO);


    // 双向映射设置 Survey 到 Question
    @AfterMapping
    default void setSurveyInQuestions(@MappingTarget Survey survey) {
        if (survey.getQuestions() != null) {
            survey.getQuestions().forEach(question -> question.setSurvey(survey));
        }
    }

    // 双向映射设置 Question 到 Option
    @AfterMapping
    default void setQuestionInOptions(@MappingTarget Survey survey) {
        if (survey.getQuestions() != null) {
            survey.getQuestions().forEach(question -> {
                if (question.getOptions() != null) {
                    question.getOptions().forEach(option -> option.setQuestion(question));
                }
            });
        }
    }
}
