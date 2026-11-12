package com.community.quiz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_score")
public class UserScore {
    @TableId
    private Long id;

    private String phone;
    private String realName;
    private Integer score;
    private Integer totalQuiz;
    private Integer correctNum;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}