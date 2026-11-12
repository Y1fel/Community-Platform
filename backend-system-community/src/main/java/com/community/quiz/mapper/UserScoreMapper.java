package com.community.quiz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.quiz.entity.UserScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserScoreMapper extends BaseMapper<UserScore> {

    @Select("SELECT * FROM user_score ORDER BY score DESC LIMIT #{count}")
    List<UserScore> selectTopScores(Integer count);

    @Select("SELECT COUNT(*) FROM user_score WHERE score > (SELECT score FROM user_score WHERE phone = #{phone})")
    Integer getRank(String phone);
}