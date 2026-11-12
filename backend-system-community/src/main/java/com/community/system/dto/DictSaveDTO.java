package com.community.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictSaveDTO {

    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @NotBlank(message = "字典名称不能为空")
    private String dictName;

    @NotBlank(message = "字典编码不能为空")
    private String dictCode;

    private Integer sort = 0;
    private String remark;
}
