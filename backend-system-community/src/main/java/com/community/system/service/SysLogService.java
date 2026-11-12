package com.community.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.system.entity.SysLog;

public interface SysLogService {

    /** 登录日志，供登录模块调用 */
    void saveLoginLog(String operator, String ip);

    /** 操作日志，供系统管理 / 工单模块调用 */
    void saveOperationLog(String operator, String module, String content, String ip);

    PageResult<SysLog> page(int page, int pageSize, String operator, String type,
                            String startTime, String endTime);
}
