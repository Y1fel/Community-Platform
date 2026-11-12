package com.community.dashboard.controller;

import com.community.common.Result;
import com.community.dashboard.service.DashBoardService;
import com.community.dashboard.vo.WorkOrderSummaryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashBoardController {
    private final DashBoardService dashboardService;

    @GetMapping("/summary")
    public Result<WorkOrderSummaryVo> summary() {
        return Result.ok(dashboardService.summary());
    }
}
