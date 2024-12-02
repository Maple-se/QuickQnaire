package com.maple.quickqnairebackend;

/**
 * Created by zong chang on 2024/12/2 16:31
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import com.maple.quickqnairebackend.dto.SurveyDTO;
import com.maple.quickqnairebackend.entity.QuestionOption;
import com.maple.quickqnairebackend.entity.Question;
import com.maple.quickqnairebackend.entity.Survey;
import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.service.OptionService;
import com.maple.quickqnairebackend.service.QuestionService;
import com.maple.quickqnairebackend.service.SurveyService;
import com.maple.quickqnairebackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
public class SurveyServiceTest {

//    @Autowired
//    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private OptionService optionService;


    private User adminUser;


    @BeforeEach
    public void setUp() {

        if(!userService.IsUserNameExist("maple")) {
            // 创建管理员用户
            adminUser = new User();
            adminUser.setUsername("maple");
            adminUser.setPassword("password123");
            adminUser.setEmail("435186@qq.com");
            adminUser.setRole(User.Role.ADMIN);
            userService.createUser(adminUser); // 假设save方法是保存用户的方法
        }
        adminUser = userService.getUserByUsername("maple");
    }

    @Test
    public void testDeleteAllSurveys() throws Exception {
        // 获取所有的 Survey
        List<Survey> surveys = surveyService.getAllSurveys();

        // 如果 Survey 列表不为空，逐个删除
        if (!surveys.isEmpty()) {
            for (Survey survey : surveys) {
                surveyService.deleteSurvey(survey.getId()); // 删除 Survey
            }

            // 验证所有 Survey 已被删除
            List<Survey> updatedSurveys = surveyService.getAllSurveys();
            assertTrue(updatedSurveys.isEmpty(), "All surveys should be deleted");
        } else {
            // 如果没有测试 Survey，直接跳过验证或输出提示
            System.out.println("No surveys found to delete");
        }
    }

    /*
    * @Transactional注解：
    * 1.如果在查询时涉及懒加载（如获取问题和选项），但查询方法没有包含在事务中，
    * 可能会导致 LazyInitializationException
    *
    * 2.@Transactional 保证事务管理
    * 如果你的查询是跨多个方法调用的，确保在服务层使用 @Transactional 注解，以保证懒加载时事务会保持有效
    *
    * 3.走了弯路：
    * 使用lazy加载时，查询关联实体务必使用@Transactional注解，若相互映射关系正确，如ManyToOne/OneToMany
    * 如：    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    * 则在查询survey时可以达到级联查询的效果，survey里的questions以及questions里的options
    * */
    @Test
    @Transactional
    public void querySurveys() throws Exception{
        /*此处注释为探索过程，应吸取经验（初作“教训二字，后觉不妥，改为经验二字”）*/
//        List<QuestionOption> options = optionService.getAllOptions();
//        List<Question> questions = questionService.getAllQuestions();

//        for (Question question : questions) {
//           // System.out.println("Question: " + question); // 确保问题加载
//            // 给每个问题添加其对应的选项
//            question.setOptions(options.stream()
//                    .filter(option -> option.getQuestion().equals(question))
//                    .collect(Collectors.toList()));
//
////            for (QuestionOption option : question.getOptions()) {
////                System.out.println(option); // 输出选项内容
////            }
//        }

        Survey queriedSurvey = surveyService.getSurveyById(8L); // 获取问卷
//        queriedSurvey.setQuestions(questions.stream()
//                .filter(question -> question.getSurvey().equals(queriedSurvey))
//                .collect(Collectors.toList()));


        // 打印出 Survey 的问题和每个问题的选项
        System.out.println("Survey contains questions: " + queriedSurvey.getQuestions().size());

        System.out.println("Survey ID: " + queriedSurvey.getId() + ", Title: " + queriedSurvey.getTitle());
        for (Question question : queriedSurvey.getQuestions()) {
            System.out.println(question);
            for (QuestionOption option : question.getOptions()) {
                System.out.println(option);
            }
        }

    }

    @Test
    public void testCreateSurvey() throws Exception {
        // 创建一个Survey对象
        Survey survey = new Survey();
        survey.setTitle("Customer Feedback Survey");
        survey.setDescription("A test survey");
        survey.setAccessLevel(Survey.AccessLevel.PUBLIC);

        //survey.setCreatedBy(adminUser);

        // 保存问卷
        SurveyDTO createdSurvey = surveyService.createSurvey(survey, adminUser.getId());

        // 创建三个问题
        createQuestion(createdSurvey, "What is your favorite color?", Question.QuestionType.SINGLE_CHOICE);
        createQuestion(createdSurvey, "What fruits do you like?", Question.QuestionType.MULTIPLE_CHOICE);
        createQuestion(createdSurvey, "Please describe your ideal vacation.", Question.QuestionType.TEXT);

        //createdSurvey.setQuestions(questions);

        // 查询问卷及其问题
        Survey queriedSurvey = surveyService.getSurveyById(createdSurvey.getId());
        assertNotNull(queriedSurvey);
        assertEquals("Customer Feedback Survey", queriedSurvey.getTitle());
        System.out.println("Survey Title: " + queriedSurvey.getTitle());
    }

    private void createQuestion(SurveyDTO survey, String content, Question.QuestionType type) {
        Question question = new Question();
        //question.setSurvey(survey);
        question.setContent(content);
        question.setType(type);
        question.setRequired(true); // 假设所有问题都是必答的

        // 创建问题并保存
        Question createdQuestion = questionService.createQuestion(survey.getId(), question);

        // 根据问题类型，创建选项（如果是单选或多选类型）
        if (type == Question.QuestionType.SINGLE_CHOICE || type == Question.QuestionType.MULTIPLE_CHOICE) {
            createOptionsForQuestion(createdQuestion);
        }
        //createdQuestion.setOptions(questionOptions);
        //questions.add(createdQuestion);
    }

    private void createOptionsForQuestion(Question question) {
        QuestionOption option1 = new QuestionOption();
        //option1.setQuestion(question);
        option1.setContent("Option 1");
        optionService.createOption(question.getId(), option1);
        //questionOptions.add(o1);

        QuestionOption option2 = new QuestionOption();
        //option2.setQuestion(question);
        option2.setContent("Option 2");
        optionService.createOption(question.getId(),option2);
        //questionOptions.add(o2);

        QuestionOption option3 = new QuestionOption();
        //option3.setQuestion(question);
        option3.setContent("Option 3");
        optionService.createOption(question.getId(),option3);
        //questionOptions.add(o3);
    }
}
