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

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // 选项属于一个问题

    @Column(nullable = false)
    private String content; // 选项内容
}

