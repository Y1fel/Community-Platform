package com.community.system.controller;

import com.community.common.PageResult;
import com.community.common.Result;
import com.community.security.SecurityUtils;
import com.community.system.dto.BatchDeleteDTO;
import com.community.system.dto.UserSaveDTO;
import com.community.system.service.SysLogService;
import com.community.system.service.SysUserService;
import com.community.system.vo.UserVO;
import com.community.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final SysLogService sysLogService;

    @GetMapping
    public Result<PageResult<UserVO>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.ok(sysUserService.page(page, pageSize, keyword));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody UserSaveDTO dto, HttpServletRequest request) {
        sysUserService.add(dto);
        sysLogService.saveOperationLog(
                SecurityUtils.currentOperator(), "用户管理",
                "新增用户：" + dto.getUsername(), IpUtils.getIp(request));
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserSaveDTO dto,
                               HttpServletRequest request) {
        sysUserService.update(id, dto);
        sysLogService.saveOperationLog(
                SecurityUtils.currentOperator(), "用户管理",
                "修改用户：" + dto.getUsername(), IpUtils.getIp(request));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        sysUserService.delete(id);
        sysLogService.saveOperationLog(
                SecurityUtils.currentOperator(), "用户管理",
                "删除用户 id=" + id, IpUtils.getIp(request));
        return Result.ok();
    }

    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@Valid @RequestBody BatchDeleteDTO dto, HttpServletRequest request) {
        sysUserService.batchDelete(dto.getIds());
        sysLogService.saveOperationLog(
                SecurityUtils.currentOperator(), "用户管理",
                "批量删除用户：" + dto.getIds(), IpUtils.getIp(request));
        return Result.ok();
    }
}
