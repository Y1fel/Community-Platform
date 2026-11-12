package com.community.quiz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.BusinessException;
import com.community.quiz.dto.ExchangeDTO;
import com.community.quiz.dto.QuizSubmitDTO;
import com.community.quiz.entity.*;
import com.community.quiz.mapper.*;
import com.community.quiz.service.QuizService;
import com.community.quiz.vo.QuestionVO;
import com.community.quiz.vo.QuizResultVO;
import com.community.quiz.vo.ScoreRankingVO;
import com.community.quiz.vo.UserScoreVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuestionMapper questionMapper;
    private final QuizRecordMapper quizRecordMapper;
    private final UserScoreMapper userScoreMapper;
    private final PrizeMapper prizeMapper;
    private final ExchangeRecordMapper exchangeRecordMapper;

    @Override
    public List<QuestionVO> getRandomQuestions(Integer count) {
        List<Question> questions = questionMapper.selectRandomQuestions(count);
        return questions.stream().map(QuestionVO::from).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizResultVO submitAnswer(QuizSubmitDTO dto) {
        Question question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) {
            throw new BusinessException("题目不存在");
        }

        boolean isCorrect = question.getAnswer().equals(dto.getAnswer());
        int score = isCorrect ? question.getScore() : 0;

        QuizRecord record = new QuizRecord();
        record.setPhone(dto.getPhone());
        record.setQuestionId(dto.getQuestionId());
        record.setUserAnswer(dto.getAnswer());
        record.setIsCorrect(isCorrect ? 1 : 0);
        record.setScore(score);
        record.setCreateTime(LocalDateTime.now());
        quizRecordMapper.insert(record);

        UserScore userScore = userScoreMapper.selectOne(
                new LambdaQueryWrapper<UserScore>().eq(UserScore::getPhone, dto.getPhone()));

        if (userScore == null) {
            userScore = new UserScore();
            userScore.setPhone(dto.getPhone());
            userScore.setRealName(dto.getRealName());
            userScore.setScore(score);
            userScore.setTotalQuiz(1);
            userScore.setCorrectNum(isCorrect ? 1 : 0);
            userScore.setCreateTime(LocalDateTime.now());
            userScore.setUpdateTime(LocalDateTime.now());
            userScoreMapper.insert(userScore);
        } else {
            userScore.setScore(userScore.getScore() + score);
            userScore.setTotalQuiz(userScore.getTotalQuiz() + 1);
            if (isCorrect) {
                userScore.setCorrectNum(userScore.getCorrectNum() + 1);
            }
            userScore.setUpdateTime(LocalDateTime.now());
            userScoreMapper.updateById(userScore);
        }

        Integer todayCount = quizRecordMapper.countTodayQuiz(dto.getPhone());

        QuizResultVO result = new QuizResultVO();
        result.setIsCorrect(isCorrect);
        result.setCorrectAnswer(question.getAnswer());
        result.setScore(score);
        result.setTotalScore(userScore.getScore());
        result.setTodayCount(todayCount);

        return result;
    }

    @Override
    public UserScoreVO getUserScore(String phone) {
        UserScore userScore = userScoreMapper.selectOne(
                new LambdaQueryWrapper<UserScore>().eq(UserScore::getPhone, phone));

        if (userScore == null) {
            return null;
        }

        Integer rank = userScoreMapper.getRank(phone);
        return UserScoreVO.from(userScore, rank != null ? rank + 1 : 0);
    }

    @Override
    public List<ScoreRankingVO> getTopScores(Integer count) {
        List<UserScore> scores = userScoreMapper.selectTopScores(count);
        List<ScoreRankingVO> result = new java.util.ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            result.add(ScoreRankingVO.from(scores.get(i), i + 1));
        }
        return result;
    }

    @Override
    public List<Prize> getActivePrizes() {
        return prizeMapper.selectList(
                new LambdaQueryWrapper<Prize>()
                        .eq(Prize::getStatus, "active")
                        .gt(Prize::getStock, 0)
                        .orderByAsc(Prize::getSort));
    }

    @Override
    public Prize getPrizeById(Long id) {
        return prizeMapper.selectById(id);
    }

    @Override
    @Transactional
    public ExchangeRecord exchangePrize(ExchangeDTO dto) {
        Prize prize = prizeMapper.selectById(dto.getPrizeId());
        if (prize == null) {
            throw new BusinessException("奖品不存在");
        }
        if (!"active".equals(prize.getStatus())) {
            throw new BusinessException("奖品已下架");
        }
        if (prize.getStock() <= 0) {
            throw new BusinessException("奖品已售罄");
        }

        UserScore userScore = userScoreMapper.selectOne(
                new LambdaQueryWrapper<UserScore>().eq(UserScore::getPhone, dto.getPhone()));

        if (userScore == null || userScore.getScore() < prize.getCost()) {
            throw new BusinessException("积分不足");
        }

        userScore.setScore(userScore.getScore() - prize.getCost());
        userScore.setUpdateTime(LocalDateTime.now());
        userScoreMapper.updateById(userScore);

        prize.setStock(prize.getStock() - 1);
        prize.setUpdateTime(LocalDateTime.now());
        prizeMapper.updateById(prize);

        ExchangeRecord record = new ExchangeRecord();
        record.setPhone(dto.getPhone());
        record.setRealName(dto.getRealName());
        record.setPrizeId(prize.getId());
        record.setPrizeName(prize.getName());
        record.setCost(prize.getCost());
        record.setStatus("pending");
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        exchangeRecordMapper.insert(record);

        return record;
    }

    @Override
    public List<ExchangeRecord> getExchangeRecords(String phone) {
        return exchangeRecordMapper.selectList(
                new LambdaQueryWrapper<ExchangeRecord>()
                        .eq(ExchangeRecord::getPhone, phone)
                        .orderByDesc(ExchangeRecord::getCreateTime));
    }
}