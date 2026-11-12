package com.community.system.controller;

import com.community.common.PageResult;
import com.community.common.Result;
import com.community.security.SecurityUtils;
import com.community.system.dto.DictSaveDTO;
import com.community.system.entity.SysDict;
import com.community.system.service.SysDictService;
import com.community.system.service.SysLogService;
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
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictService sysDictService;
    private final SysLogService sysLogService;

    @GetMapping
    public Result<PageResult<SysDict>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.ok(sysDictService.page(page, pageSize, keyword));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody DictSaveDTO dto, HttpServletRequest request) {
        sysDictService.add(dto);
        sysLogService.saveOperationLog(
                SecurityUtils.currentOperator(), "数据字典",
                "新增字典：" + dto.getDictType() + "/" + dto.getDictCode(), IpUtils.getIp(request));
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody DictSaveDTO dto,
                               HttpServletRequest request) {
        sysDictService.update(id, dto);
        sysLogService.saveOperationLog(
                SecurityUtils.currentOperator(), "数据字典",
                "修改字典 id=" + id, IpUtils.getIp(request));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        sysDictService.delete(id);
        sysLogService.saveOperationLog(
                SecurityUtils.currentOperator(), "数据字典",
                "删除字典 id=" + id, IpUtils.getIp(request));
        return Result.ok();
    }
}
