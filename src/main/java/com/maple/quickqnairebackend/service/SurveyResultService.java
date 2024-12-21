package com.maple.quickqnairebackend.service;

/**
 * Created by zong chang on 2024/12/3 18:19
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.dto.SurveyResultDTO;
import com.maple.quickqnairebackend.entity.SurveyResult;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.mapper.SurveyResultMapper;
import com.maple.quickqnairebackend.repository.SurveyResultRepository;
import com.maple.quickqnairebackend.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SurveyResultService {

    private final SurveyResultRepository surveyResultRepository;

    private  final SurveyResultMapper surveyResultMapper;


    // 保存一个新的 SurveyResult
    public SurveyResult saveSurveyResult(SurveyResultDTO surveyResult) {
        // 如果是已登录用户，直接关联 User
        return surveyResultRepository.save(dtoToSurveyResult(surveyResult));
    }

    // 根据 Survey ID 和 User ID 获取已提交的问卷结果
    public SurveyResult getSurveyResult(Long surveyId, Long userId) {
        Optional<SurveyResult> surveyResult = surveyResultRepository.findBySurveyIdAndUserId(surveyId, userId);
        return surveyResult.orElse(null);  // 如果未找到结果，则返回 null
    }

    // 获取匿名用户的问卷结果
//    public SurveyResult getSurveyResultForAnonymous(String surveyId, String anonymousId) {
//        Optional<SurveyResult> surveyResult = surveyResultRepository.findBySurveyIdAndAnonymousId(surveyId, anonymousId);
//        return surveyResult.orElse(null);
//    }

    // 获取用户提交的所有问卷结果
    public List<SurveyResult> getSurveyResultsByUserId(Long userId) {
        return surveyResultRepository.findByUserId(userId);
    }


    private  SurveyResult dtoToSurveyResult(SurveyResultDTO surveyResultDTO){
       return surveyResultMapper.toEntity(surveyResultDTO);
    }
}
