package com.maple.quickqnairebackend.service;

/**
 * Created by zong chang on 2024/12/2 16:25
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.dto.OptionDTO;
import com.maple.quickqnairebackend.entity.QuestionOption;
import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.repository.OptionRepository;
import com.maple.quickqnairebackend.repository.QuestionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
public class OptionService {

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // 创建新的选项
    //ToDo:是否需要返回实体
    @Transactional
    public QuestionOption createOption(Long questionId, @Valid OptionDTO odto) {
        // 查找问题
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isPresent()) {
            Question question = questionOptional.get();
            QuestionOption option = toEntity(odto);
            option.setQuestion(question);  // 设置选项属于该问题
            return optionRepository.save(option);
        } else {
            throw new IllegalArgumentException("Question not found with id " + questionId);
        }
    }

    private QuestionOption toEntity(OptionDTO dto) {
        QuestionOption questionOption = new QuestionOption();
        questionOption.setOptionContent(dto.getOptionContent());
        return questionOption;
    }

    // 处理选项列表
    @Transactional
    public void processOption(Question question, OptionDTO optionDTO) {

            if (optionDTO.getOptionId() != null) {
                // 更新现有选项
                QuestionOption existingOption = getOptionById(optionDTO.getOptionId());
                if (existingOption != null) {
                    updateOption(existingOption, optionDTO);
                } else {
                    throw new IllegalArgumentException("Option ID " + optionDTO.getOptionId() + " not found");
                }
            } else {
                // 新增选项
                createOption(question.getId(), optionDTO);
            }
    }

    // 更新选项
    @Transactional
    public QuestionOption updateOption(QuestionOption option, OptionDTO optionUpdateDTO) {
        if (StringUtils.isNotBlank(optionUpdateDTO.getOptionContent())) option.setOptionContent(optionUpdateDTO.getOptionContent());
        return optionRepository.save(option);
    }

    // 删除选项
    @Transactional
    public void deleteOption(Long optionId) {
        if (optionRepository.existsById(optionId)) {
            optionRepository.deleteById(optionId);
        } else {
            throw new IllegalArgumentException("Option not found with id " + optionId);
        }
    }

    // 获取所有选项
    public List<QuestionOption> getAllOptions() {
        return optionRepository.findAll();
    }

    // 根据问题id获取该问题的所有选项
    public List<QuestionOption> getOptionsByQuestion(Long questionId) {
        return optionRepository.findByQuestionId(questionId);
    }

    // 获取单个选项
    public QuestionOption getOptionById(Long optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("Option not found with id " + optionId));
    }
}
