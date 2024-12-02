package com.maple.quickqnairebackend.entity;

/**
 * Created by zong chang on 2024/11/29 16:38
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // 选项属于一个问题

    @Setter
    @Column(nullable = false)
    private String content; // 选项内容

    @Override
    public String toString() {
        return "Option{id=" + id + ", content='" + content + "'}";
    }
}

