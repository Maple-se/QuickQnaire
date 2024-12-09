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

    // 转换 SurveyDTO 为 Survey（在更新时可能用到）

    Survey toSurvey(SurveyDTO surveyDTO);
}
