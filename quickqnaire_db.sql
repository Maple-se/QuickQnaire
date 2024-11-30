-- 创建数据库
CREATE DATABASE IF NOT EXISTS `quickqnaire_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quickqnaire_db`;

-- 创建用户表（users）
CREATE TABLE IF NOT EXISTS `users` (
   `id` BIGINT AUTO_INCREMENT PRIMARY KEY,                -- 自增主键
   `username` VARCHAR(50) NOT NULL UNIQUE,                 -- 用户名，唯一且非空
    `password` VARCHAR(255) NOT NULL,                       -- 密码，非空
    `email` VARCHAR(100) UNIQUE,                            -- 邮箱，唯一
    `role` ENUM('ADMIN', 'USER') NOT NULL,                  -- 用户角色，枚举类型
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 创建时间，默认当前时间
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新时间，自动更新
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建问卷表（surveys）
CREATE TABLE IF NOT EXISTS `surveys` (
     `id` BIGINT AUTO_INCREMENT PRIMARY KEY,                -- 自增主键
     `title` VARCHAR(255) NOT NULL,                          -- 问卷标题，非空
    `description` TEXT,                                     -- 问卷描述
    `status` ENUM('DRAFT', 'PENDING_APPROVAL', 'ACTIVE', 'CLOSED') NOT NULL, -- 问卷状态
    `created_by` BIGINT NOT NULL,                           -- 创建者的用户ID
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 更新时间
    `active_start_date` TIMESTAMP,                          -- 问卷被管理员批准并发布的时间
    `duration` INT,                                         -- 系统默认持续时间（单位：小时）
    `user_set_duration` INT,                                -- 用户自定义持续时间（单位：小时）
    `max_responses` INT,                                    -- 最大回答数
    `responses_received` INT NOT NULL DEFAULT 0,            -- 已收到的回答数
    `access_level` ENUM('PUBLIC', 'PRIVATE', 'RESTRICTED') NOT NULL, -- 问卷访问控制级别
    CONSTRAINT `FK_surveys_created_by` FOREIGN KEY (`created_by`) REFERENCES `users`(`id`) ON DELETE CASCADE, -- 外键关联到 users 表
    INDEX `idx_status` (`status`)                           -- 为 status 字段创建索引（可选）
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建问题表（questions）
CREATE TABLE IF NOT EXISTS `questions` (
   `id` BIGINT AUTO_INCREMENT PRIMARY KEY,                -- 自增主键
   `survey_id` BIGINT NOT NULL,                            -- 问卷ID（外键，关联到 surveys）
   `content` TEXT NOT NULL,                                -- 问题内容，非空
   `type` ENUM('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'TEXT') NOT NULL, -- 问题类型（单选、多选、文本）
    `required` BOOLEAN NOT NULL,                            -- 是否为必答问题
    CONSTRAINT `FK_questions_survey_id` FOREIGN KEY (`survey_id`) REFERENCES `surveys`(`id`) ON DELETE CASCADE, -- 外键关联到 surveys 表
    INDEX `idx_survey_id` (`survey_id`)                     -- 为 survey_id 创建索引（可选）
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建选项表（options）
CREATE TABLE IF NOT EXISTS `options` (
 `id` BIGINT AUTO_INCREMENT PRIMARY KEY,                -- 自增主键
 `question_id` BIGINT NOT NULL,                          -- 外键，关联到 questions 表的 id
 `content` VARCHAR(255) NOT NULL,                         -- 选项内容
CONSTRAINT `FK_options_question_id` FOREIGN KEY (`question_id`) REFERENCES `questions`(`id`) ON DELETE CASCADE -- 外键约束，删除问题时同时删除选项
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建问卷提交结果表（survey_results）
CREATE TABLE IF NOT EXISTS `survey_results` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,                -- SurveyResult 的唯一标识
    `survey_id` BIGINT NOT NULL,                            -- 外键，关联到 surveys 表的 id
    `user_id` BIGINT,                                       -- 外键，关联到 users 表的 id，允许为空
    `anonymous_id` VARCHAR(255),                            -- 匿名用户标识，允许为空
    `submitted_at` TIMESTAMP NOT NULL,                      -- 提交时间
    CONSTRAINT `FK_survey_results_survey_id` FOREIGN KEY (`survey_id`) REFERENCES `surveys`(`id`) ON DELETE CASCADE, -- 关联 surveys 表的外键约束
    CONSTRAINT `FK_survey_results_user_id` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL -- 关联 users 表的外键约束，用户删除时设置为 NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建问题回答结果表（question_results）
CREATE TABLE IF NOT EXISTS `question_results` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,                -- QuestionResult 的唯一标识
  `survey_result_id` BIGINT NOT NULL,                     -- 外键，关联到 survey_results 表的 id
  `question_id` BIGINT NOT NULL,                          -- 外键，关联到 questions 表的 id
  `answer` TEXT NOT NULL,                                 -- 问题的答案
  CONSTRAINT `FK_question_results_survey_result_id` FOREIGN KEY (`survey_result_id`) REFERENCES `survey_results`(`id`) ON DELETE CASCADE, -- 外键关联到 survey_results 表
    CONSTRAINT `FK_question_results_question_id` FOREIGN KEY (`question_id`) REFERENCES `questions`(`id`) ON DELETE CASCADE -- 外键关联到 questions 表
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
