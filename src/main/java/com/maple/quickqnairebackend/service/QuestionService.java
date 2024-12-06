package com.maple.quickqnairebackend.service;

/**
 * Created by zong chang on 2024/12/2 16:11
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.dto.OptionDTO;
import com.maple.quickqnairebackend.dto.QuestionDTO;
import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.repository.QuestionRepository;
import com.maple.quickqnairebackend.repository.SurveyRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private OptionService optionService;

    // 创建新的问题
    @Transactional
    public Question createQuestion(Long surveyId,@Valid QuestionDTO qdto) {
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


    private Question toEntity(QuestionDTO dto) {
        Question question = new Question();
        question.setQuestionContent(dto.getQuestionContent());
        question.setType(dto.getQuestionType());
        question.setRequired(dto.getRequired());
        return question;
    }


    // 处理问题列表
    @Transactional
    public Question processQuestion(Survey survey, QuestionDTO questionDTO) {
        Question updatedQuestion = new Question();
        // 处理请求中的问题
            if (questionDTO.getQuestionId() != null) {
                // 更新现有问题
                Question existingQuestion = getQuestionById(questionDTO.getQuestionId());
                if (existingQuestion != null) {
                  updatedQuestion = updateQuestion(existingQuestion, questionDTO);
                } else {
                    throw new IllegalArgumentException("Question ID " + questionDTO.getQuestionId() + " not found");
                }
            } else {
                // 新增问题
                updatedQuestion = createQuestion(survey.getId(), questionDTO);
                // 根据问题类型，创建选项（如果是单选或多选类型）
//                if (questionDTO.getQuestionType() == Question.QuestionType.SINGLE_CHOICE || questionDTO.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE) {
//                    for (OptionDTO odto: questionDTO.getOptions()) {
//                        optionService.createOption(updatedQuestion.getId(),odto);
//                    }
//                }
            }
            return updatedQuestion;
    }


    // 更新问题
    @Transactional
    public Question updateQuestion(Question question, QuestionDTO questionUpdateDTO) {
        if (StringUtils.isNotBlank(questionUpdateDTO.getQuestionContent())) question.setQuestionContent(questionUpdateDTO.getQuestionContent());
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
