package com.community.quiz.vo;

import com.community.quiz.entity.Question;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class QuestionVO {
    private Long id;
    private String content;
    private List<String> options;
    private String answer;
    private String category;
    private Integer difficulty;
    private Integer score;

    public static QuestionVO from(Question question) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setContent(question.getContent());
        vo.setOptions(Arrays.asList(question.getOptions().split("\\|\\|\\|")));
        vo.setAnswer(question.getAnswer());
        vo.setCategory(question.getCategory());
        vo.setDifficulty(question.getDifficulty());
        vo.setScore(question.getScore());
        return vo;
    }
}