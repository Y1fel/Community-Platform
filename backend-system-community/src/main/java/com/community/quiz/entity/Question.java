package com.community.quiz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("quiz_question")
public class Question {
    @TableId
    private Long id;

    private String content;
    private String options;
    private String answer;
    private String category;
    private Integer difficulty;
    private Integer score;
    private LocalDateTime createTime;
}