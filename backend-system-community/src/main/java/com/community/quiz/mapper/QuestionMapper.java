package com.community.quiz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.quiz.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    @Select("SELECT * FROM quiz_question ORDER BY RAND() LIMIT #{count}")
    List<Question> selectRandomQuestions(Integer count);

    @Select("SELECT * FROM quiz_question WHERE category = #{category} ORDER BY RAND() LIMIT #{count}")
    List<Question> selectRandomByCategory(String category, Integer count);
}