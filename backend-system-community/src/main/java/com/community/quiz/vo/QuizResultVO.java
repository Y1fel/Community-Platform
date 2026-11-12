package com.community.quiz.vo;

import lombok.Data;

@Data
public class QuizResultVO {
    private Boolean isCorrect;
    private String correctAnswer;
    private Integer score;
    private Integer totalScore;
    private Integer todayCount;
}