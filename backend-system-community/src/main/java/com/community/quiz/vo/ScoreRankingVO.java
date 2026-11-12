package com.community.quiz.vo;

import com.community.quiz.entity.UserScore;
import lombok.Data;

@Data
public class ScoreRankingVO {
    private Integer rank;
    private String phone;
    private String realName;
    private Integer score;
    private Integer totalQuiz;
    private Integer correctNum;

    public static ScoreRankingVO from(UserScore score, Integer rank) {
        ScoreRankingVO vo = new ScoreRankingVO();
        vo.setRank(rank);
        vo.setPhone(score.getPhone());
        vo.setRealName(score.getRealName());
        vo.setScore(score.getScore());
        vo.setTotalQuiz(score.getTotalQuiz());
        vo.setCorrectNum(score.getCorrectNum());
        return vo;
    }
}