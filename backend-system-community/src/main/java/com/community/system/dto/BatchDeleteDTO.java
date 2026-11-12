package com.community.system.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BatchDeleteDTO {

    @NotEmpty(message = "ids 不能为空")
    private List<Long> ids;
}
