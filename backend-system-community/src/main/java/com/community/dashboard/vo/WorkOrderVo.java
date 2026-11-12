package com.community.dashboard.vo;

import com.community.workorder.entity.WorkOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkOrderVo {
    private Long id;
    private String reporter;
    private String phone;
    private String address;
    private String description;
    private String status;
    private String handleType;
    private String handleResult;
    private LocalDateTime completeTime;
    private LocalDateTime createTime;

    public static WorkOrderVo from(WorkOrder workOrder) {
        WorkOrderVo vo = new WorkOrderVo();
        vo.setId(workOrder.getId());
        vo.setReporter(workOrder.getReporter());
        vo.setPhone(workOrder.getPhone());
        vo.setAddress(workOrder.getAddress());
        vo.setDescription(workOrder.getDescription());
        vo.setStatus(workOrder.getStatus());
        vo.setHandleType(workOrder.getHandleType());
        vo.setHandleResult(workOrder.getHandleResult());
        vo.setCompleteTime(workOrder.getCompleteTime());
        vo.setCreateTime(workOrder.getCreateTime());
        return vo;
    }
}
