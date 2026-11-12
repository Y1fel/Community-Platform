package com.community.quiz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.quiz.entity.Question;
import com.community.quiz.entity.QuizRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuizRecordMapper extends BaseMapper<QuizRecord> {

    @Select("SELECT COUNT(*) FROM quiz_record WHERE phone = #{phone} AND DATE(create_time) = DATE(NOW())")
    Integer countTodayQuiz(String phone);

    @Select("SELECT q.* FROM quiz_record r JOIN quiz_question q ON r.question_id = q.id " +
            "WHERE r.phone = #{phone} ORDER BY r.create_time DESC LIMIT #{count}")
    List<Question> selectRecentQuestions(String phone, Integer count);
}