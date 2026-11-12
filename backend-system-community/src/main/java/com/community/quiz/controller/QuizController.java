package com.community.quiz.controller;

import com.community.common.Result;
import com.community.quiz.dto.ExchangeDTO;
import com.community.quiz.dto.QuizSubmitDTO;
import com.community.quiz.entity.ExchangeRecord;
import com.community.quiz.entity.Prize;
import com.community.quiz.service.QuizService;
import com.community.quiz.vo.QuestionVO;
import com.community.quiz.vo.QuizResultVO;
import com.community.quiz.vo.ScoreRankingVO;
import com.community.quiz.vo.UserScoreVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/questions")
    public Result<List<QuestionVO>> getRandomQuestions(@RequestParam(defaultValue = "5") Integer count) {
        return Result.ok(quizService.getRandomQuestions(count));
    }

    @PostMapping("/submit")
    public Result<QuizResultVO> submitAnswer(@Valid @RequestBody QuizSubmitDTO dto) {
        return Result.ok(quizService.submitAnswer(dto));
    }

    @GetMapping("/score")
    public Result<UserScoreVO> getUserScore(@RequestParam String phone) {
        return Result.ok(quizService.getUserScore(phone));
    }

    @GetMapping("/ranking")
    public Result<List<ScoreRankingVO>> getTopScores(@RequestParam(defaultValue = "10") Integer count) {
        return Result.ok(quizService.getTopScores(count));
    }

    @GetMapping("/prizes")
    public Result<List<Prize>> getActivePrizes() {
        return Result.ok(quizService.getActivePrizes());
    }

    @PostMapping("/exchange")
    public Result<ExchangeRecord> exchangePrize(@Valid @RequestBody ExchangeDTO dto) {
        return Result.ok(quizService.exchangePrize(dto));
    }

    @GetMapping("/exchanges")
    public Result<List<ExchangeRecord>> getExchangeRecords(@RequestParam String phone) {
        return Result.ok(quizService.getExchangeRecords(phone));
    }
}