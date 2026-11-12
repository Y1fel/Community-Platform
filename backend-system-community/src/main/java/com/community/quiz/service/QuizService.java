package com.community.quiz.service;

import com.community.quiz.dto.ExchangeDTO;
import com.community.quiz.dto.QuizSubmitDTO;
import com.community.quiz.entity.ExchangeRecord;
import com.community.quiz.entity.Prize;
import com.community.quiz.entity.UserScore;
import com.community.quiz.vo.QuestionVO;
import com.community.quiz.vo.QuizResultVO;
import com.community.quiz.vo.ScoreRankingVO;
import com.community.quiz.vo.UserScoreVO;

import java.util.List;

public interface QuizService {

    List<QuestionVO> getRandomQuestions(Integer count);

    QuizResultVO submitAnswer(QuizSubmitDTO dto);

    UserScoreVO getUserScore(String phone);

    List<ScoreRankingVO> getTopScores(Integer count);

    List<Prize> getActivePrizes();

    Prize getPrizeById(Long id);

    ExchangeRecord exchangePrize(ExchangeDTO dto);

    List<ExchangeRecord> getExchangeRecords(String phone);
}