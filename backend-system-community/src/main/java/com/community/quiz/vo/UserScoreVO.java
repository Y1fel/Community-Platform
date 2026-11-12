package com.community.quiz.vo;

import com.community.quiz.entity.UserScore;
import lombok.Data;

@Data
public class UserScoreVO {
    private String phone;
    private String realName;
    private Integer score;
    private Integer totalQuiz;
    private Integer correctNum;
    private Integer rank;

    public static UserScoreVO from(UserScore score, Integer rank) {
        UserScoreVO vo = new UserScoreVO();
        vo.setPhone(score.getPhone());
        vo.setRealName(score.getRealName());
        vo.setScore(score.getScore());
        vo.setTotalQuiz(score.getTotalQuiz());
        vo.setCorrectNum(score.getCorrectNum());
        vo.setRank(rank);
        return vo;
    }
}