package com.community.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.system.entity.SysLog;
import com.community.system.mapper.SysLogMapper;
import com.community.system.service.SysLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SysLogServiceImpl implements SysLogService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SysLogMapper sysLogMapper;

    @Override
    public void saveLoginLog(String operator, String ip) {
        SysLog log = new SysLog();
        log.setOperator(operator);
        log.setType("login");
        log.setModule("登录");
        log.setContent("用户登录系统");
        log.setIp(ip);
        log.setCreateTime(LocalDateTime.now());
        sysLogMapper.insert(log);
    }

    @Override
    public void saveOperationLog(String operator, String module, String content, String ip) {
        SysLog log = new SysLog();
        log.setOperator(operator);
        log.setType("operation");
        log.setModule(module);
        log.setContent(content);
        log.setIp(ip);
        log.setCreateTime(LocalDateTime.now());
        sysLogMapper.insert(log);
    }

    @Override
    public PageResult<SysLog> page(int page, int pageSize, String operator, String type,
                                   String startTime, String endTime) {
        LambdaQueryWrapper<SysLog> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(operator)) {
            qw.like(SysLog::getOperator, operator);
        }
        if (StringUtils.hasText(type)) {
            qw.eq(SysLog::getType, type);
        }
        if (StringUtils.hasText(startTime)) {
            qw.ge(SysLog::getCreateTime, LocalDateTime.parse(startTime, FMT));
        }
        if (StringUtils.hasText(endTime)) {
            qw.le(SysLog::getCreateTime, LocalDateTime.parse(endTime, FMT));
        }
        qw.orderByDesc(SysLog::getCreateTime);

        Page<SysLog> p = sysLogMapper.selectPage(new Page<>(page, pageSize), qw);
        return new PageResult<>(p.getRecords(), p.getTotal());
    }
}
