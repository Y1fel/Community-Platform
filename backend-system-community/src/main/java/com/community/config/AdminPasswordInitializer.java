package com.community.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.system.entity.SysUser;
import com.community.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时确保 admin 密码为 123456（BCrypt），避免 SQL 里写死的 hash 与库不一致。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPasswordInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        SysUser admin = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin")
        );
        if (admin == null) {
            log.warn("未找到 admin 用户，请先执行 schema.sql");
            return;
        }
        // 每次启动重置为 123456，方便开发联调；上线前请删除本类或改为仅首次初始化
        admin.setPassword(passwordEncoder.encode("123456"));
        sysUserMapper.updateById(admin);
        log.info("已确保 admin 密码为 123456（开发环境）");
    }
}
