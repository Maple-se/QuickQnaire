package com.maple.quickqnairebackend.service;

/**
 * Created by zong chang on 2024/12/2 16:11
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.dto.QuestionCreationDTO;
import com.maple.quickqnairebackend.dto.SurveyCreationDTO;
import com.maple.quickqnairebackend.dto.SurveyUpdateDTO;
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
    public Question createQuestion(Long surveyId, QuestionCreationDTO qdto) {
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


    private Question toEntity(QuestionCreationDTO dto) {
        Question question = new Question();
        question.setQuestionContent(dto.getQuestionContent());
        question.setType(dto.getQuestionType());
        question.setRequired(dto.getRequired());
        return question;
    }

    // 更新问题
    //ToDo:QuestionType更改逻辑需重新考虑
    @Transactional
    public Question updateQuestion(Question question, SurveyUpdateDTO.QuestionUpdateDTO questionUpdateDTO) {
        if (questionUpdateDTO.getQuestionText() != null) question.setQuestionContent(questionUpdateDTO.getQuestionText());
        //ToDo
        if (questionUpdateDTO.getQuestionType() != null) question.setType(questionUpdateDTO.getQuestionType());
        if (questionUpdateDTO.getRequired() != null) question.setRequired(questionUpdateDTO.getRequired());
        return questionRepository.save(question);
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
