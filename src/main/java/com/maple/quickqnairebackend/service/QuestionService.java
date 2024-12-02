package com.maple.quickqnairebackend.service;

/**
 * Created by zong chang on 2024/12/2 16:11
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.repository.QuestionRepository;
import com.maple.quickqnairebackend.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    // 创建新的问题
    @Transactional
    public Question createQuestion(Long surveyId, Question question) {
        // 查找问卷
        Optional<Survey> surveyOptional = surveyRepository.findById(surveyId);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            question.setSurvey(survey);  // 设置问题属于该问卷
            return questionRepository.save(question);
        } else {
            throw new IllegalArgumentException("Survey not found with id " + surveyId);
        }
    }

    // 更新问题
    //ToDo:更新逻辑需充分考虑
    @Transactional
    public Question updateQuestion(Long questionId, Question updatedQuestion) {
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isPresent()) {
            Question question = questionOptional.get();
            question.setContent(updatedQuestion.getContent());
            question.setType(updatedQuestion.getType());
            question.setRequired(updatedQuestion.getRequired());
            question.setOptions(updatedQuestion.getOptions());  // 更新选项
            return questionRepository.save(question);
        } else {
            throw new IllegalArgumentException("Question not found with id " + questionId);
        }
    }

    // 删除问题
    @Transactional
    public void deleteQuestion(Long questionId) {
        if (questionRepository.existsById(questionId)) {
            questionRepository.deleteById(questionId);
        } else {
            throw new IllegalArgumentException("Question not found with id " + questionId);
        }
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
