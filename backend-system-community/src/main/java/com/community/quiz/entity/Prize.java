package com.community.quiz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("prize")
public class Prize {
    @TableId
    private Long id;

    private String name;
    private String description;
    private String imageUrl;
    private Integer cost;
    private Integer stock;
    private String status;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}