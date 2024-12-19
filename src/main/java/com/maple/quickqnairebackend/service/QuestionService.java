package com.maple.quickqnairebackend.service;

/**
 * Created by zong chang on 2024/12/2 16:11
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */

import com.maple.quickqnairebackend.dto.QuestionDTO;
import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.entity.QuestionOption;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.repository.QuestionRepository;
import com.maple.quickqnairebackend.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {


    private final QuestionRepository questionRepository;

    private final SurveyRepository surveyRepository;


    // 创建新的问题
    @Transactional
    public Question createQuestion(Long surveyId,QuestionDTO qdto) {
        // 查找问卷
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            Question question = toEntity(qdto);
            question.setSurvey(survey);  // 设置问题属于该问卷
            return questionRepository.save(question);
        } else {
            throw new IllegalArgumentException("Survey not found with id " + surveyId);
        }
    }


    //
    private Question toEntity(QuestionDTO dto) {
        Question question = new Question();
        question.setQuestionContent(dto.getQuestionContent());
        question.setType(dto.getType());
        question.setRequired(dto.getRequired());
        //已解决
//        if(dto.getQuestionType()== Question.QuestionType.TEXT){
            List<QuestionOption> questionOptions = new ArrayList<>();
            question.setOptions(questionOptions);

        return question;
    }



    // 更新问题
//    @Transactional
//    public Question updateQuestion(Question question, QuestionDTO questionUpdateDTO) {
//        question.setQuestionContent(questionUpdateDTO.getQuestionContent());
//        //要么删、要么增、问题类型不必变更
//        //if (questionUpdateDTO.getQuestionType() != null) question.setType(questionUpdateDTO.getQuestionType());
//        if (questionUpdateDTO.getRequired() != null) question.setRequired(questionUpdateDTO.getRequired());
//        return questionRepository.save(question);
//    }



    // 删除问题
    @Transactional
    public void deleteQuestion(Long questionId) {
        if (questionRepository.existsById(questionId)) {
            questionRepository.deleteById(questionId);
        } else {
            throw new IllegalArgumentException("Question not found with id " + questionId);
        }
    }

    //判断特定 Survey 中是否存在指定问题
    public boolean IsQuestionExistInSurvey(Long questionId, Long surveyId){
        return questionRepository.existsByIdAndSurveyId(questionId,surveyId);
    }

    // 获取所有问题
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // 根据问卷id获取所有问题
    public List<Question> getQuestionsBySurvey(Long surveyId) {
        return questionRepository.findBySurveyId(surveyId);
    }

    // 获取单个问题
    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id " + questionId));
    }
}
