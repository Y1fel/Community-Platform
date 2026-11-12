package com.community.quiz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("quiz_record")
public class QuizRecord {
    @TableId
    private Long id;

    private String phone;
    private Long questionId;
    private String userAnswer;
    private Integer isCorrect;
    private Integer score;
    private LocalDateTime createTime;
}