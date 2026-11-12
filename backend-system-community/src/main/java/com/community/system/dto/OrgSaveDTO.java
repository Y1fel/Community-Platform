package com.community.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrgSaveDTO {

    private Long parentId;

    @NotBlank(message = "组织名称不能为空")
    private String name;

    @NotBlank(message = "组织编码不能为空")
    private String code;

    private Integer sort = 0;
}
