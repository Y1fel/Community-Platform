package com.community.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizSubmitDTO {
    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    @NotBlank(message = "答案不能为空")
    private String answer;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "姓名不能为空")
    private String realName;
}