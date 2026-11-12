package com.community.quiz.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exchange_record")
public class ExchangeRecord {
    @TableId
    private Long id;

    private String phone;
    private String realName;
    private Long prizeId;
    private String prizeName;
    private Integer cost;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}