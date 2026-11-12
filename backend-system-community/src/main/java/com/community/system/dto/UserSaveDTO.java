package com.community.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSaveDTO {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    /** 新增必填，编辑不传 */
    private String password;

    @NotNull(message = "所属组织不能为空")
    private Long orgId;

    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(admin|staff)$", message = "角色只能是 admin 或 staff")
    private String role;

    /** 编辑时可传 enabled / disabled */
    private String status;
}
