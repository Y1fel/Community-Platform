package com.community.workorder.controller;

import com.community.common.PageResult;
import com.community.common.Result;
import com.community.workorder.dto.AuditRejectRequest;
import com.community.workorder.dto.HandleWorkOrderRequest;
import com.community.workorder.dto.SubmitWorkOrderRequest;
import com.community.workorder.dto.WorkOrderQuery;
import com.community.workorder.service.WorkOrderService;
import com.community.workorder.vo.WorkOrderVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workorder")
@RequiredArgsConstructor
public class WorkOrderController {
    private final WorkOrderService workOrderService;

    @GetMapping("/todo")
    public Result<PageResult<WorkOrderVo>> todo(WorkOrderQuery query) {
        return Result.ok(workOrderService.todo(query));
    }

    @GetMapping("/audit")
    public Result<PageResult<WorkOrderVo>> audit(WorkOrderQuery query) {
        return Result.ok(workOrderService.audit(query));
    }

    @GetMapping("/done")
    public Result<PageResult<WorkOrderVo>> done(WorkOrderQuery query) {
        return Result.ok(workOrderService.done(query));
    }

    @GetMapping("/query")
    public Result<PageResult<WorkOrderVo>> query(WorkOrderQuery query) {
        return Result.ok(workOrderService.query(query));
    }

    @GetMapping("/{id}")
    public Result<WorkOrderVo> detail(@PathVariable Long id) {
        return Result.ok(workOrderService.detail(id));
    }

    @PostMapping("/{id}/handle")
    public Result<Void> handle(@PathVariable Long id, @Valid @RequestBody HandleWorkOrderRequest request) {
        workOrderService.handle(id, request);
        return Result.ok();
    }

    @PostMapping("/{id}/audit-pass")
    public Result<Void> auditPass(@PathVariable Long id) {
        workOrderService.auditPass(id);
        return Result.ok();
    }

    @PostMapping("/{id}/audit-reject")
    public Result<Void> auditReject(@PathVariable Long id, @Valid @RequestBody AuditRejectRequest request) {
        workOrderService.auditReject(id, request);
        return Result.ok();
    }

    @PostMapping("/submit")
    public Result<WorkOrderVo> submit(@Valid @RequestBody SubmitWorkOrderRequest request) {
        return Result.ok(workOrderService.submit(request));
    }
}
