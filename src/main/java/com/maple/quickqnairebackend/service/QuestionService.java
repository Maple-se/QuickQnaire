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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private EntityManager entityManager;


    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SurveyRepository surveyRepository;


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


    // 处理问题列表
    @Transactional
    public Question processQuestion(Long surveyId, QuestionDTO questionDTO) {
        Question updatedQuestion = new Question();
        // 处理请求中的问题
            if (questionDTO.getQuestionId() != null) {
                // 使用 existsBy 来判断问题是否存在于该 Survey 中
                boolean exists = questionRepository.existsByIdAndSurveyId(questionDTO.getQuestionId(), surveyId);

                if (exists) {
                    // 如果存在，更新问题
                    Question existingQuestion = getQuestionById(questionDTO.getQuestionId());
                    updatedQuestion = updateQuestion(existingQuestion, questionDTO);
                } else {
                    // 如果不存在，抛出异常
                    throw new IllegalArgumentException("Question ID " + questionDTO.getQuestionId() + " not found in this survey");
                }
            } else {
                // 新增问题
                updatedQuestion = createQuestion(surveyId, questionDTO);
            }
            //强刷数据库，确保最新数据已保存
            entityManager.flush();
            entityManager.clear();
            return updatedQuestion;
    }


    // 更新问题
    @Transactional
    public Question updateQuestion(Question question, QuestionDTO questionUpdateDTO) {
        question.setQuestionContent(questionUpdateDTO.getQuestionContent());
        //要么删、要么增、问题类型不必变更
        //if (questionUpdateDTO.getQuestionType() != null) question.setType(questionUpdateDTO.getQuestionType());
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
