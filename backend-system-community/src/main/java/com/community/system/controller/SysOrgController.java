package com.community.system.controller;

import com.community.common.Result;
import com.community.security.SecurityUtils;
import com.community.system.dto.OrgSaveDTO;
import com.community.system.service.SysLogService;
import com.community.system.service.SysOrgService;
import com.community.system.vo.OrgVO;
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

import java.util.List;

@RestController
@RequestMapping("/api/system/org")
@RequiredArgsConstructor
public class SysOrgController {

    private final SysOrgService sysOrgService;
    private final SysLogService sysLogService;

    @GetMapping
    public Result<List<OrgVO>> tree(@RequestParam(required = false) String keyword) {
        return Result.ok(sysOrgService.tree(keyword));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody OrgSaveDTO dto, HttpServletRequest request) {
        sysOrgService.add(dto);
        sysLogService.saveOperationLog(
                SecurityUtils.currentOperator(), "组织管理",
                "新增组织：" + dto.getName(), IpUtils.getIp(request));
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody OrgSaveDTO dto,
                               HttpServletRequest request) {
        sysOrgService.update(id, dto);
        sysLogService.saveOperationLog(
                SecurityUtils.currentOperator(), "组织管理",
                "修改组织：" + dto.getName(), IpUtils.getIp(request));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        sysOrgService.delete(id);
        sysLogService.saveOperationLog(
                SecurityUtils.currentOperator(), "组织管理",
                "删除组织 id=" + id, IpUtils.getIp(request));
        return Result.ok();
    }
}
