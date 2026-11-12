package com.community.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.BusinessException;
import com.community.common.Result;
import com.community.security.JwtTokenProvider;
import com.community.system.entity.SysOrg;
import com.community.system.entity.SysUser;
import com.community.system.mapper.SysOrgMapper;
import com.community.system.mapper.SysUserMapper;
import com.community.system.service.SysLogService;
import com.community.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 临时登录接口：方便联调系统管理模块。
 * 登录模块同事正式实现后，可对齐并复用同一套 JWT。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserMapper sysUserMapper;
    private final SysOrgMapper sysOrgMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SysLogService sysLogService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        if (req.getUsername() == null || req.getUsername().isBlank()
                || req.getPassword() == null || req.getPassword().isBlank()) {
            throw new BusinessException("用户名和密码不能为空");
        }
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, req.getUsername())
        );
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!"enabled".equals(user.getStatus())) {
            throw new BusinessException("账号已禁用");
        }

        String token = jwtTokenProvider.createToken(user.getId(), user.getUsername(), user.getRealName(), user.getRole());
        sysLogService.saveLoginLog(user.getRealName(), IpUtils.getIp(request));

        SysOrg org = sysOrgMapper.selectById(user.getOrgId());
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());
        userInfo.put("orgId", user.getOrgId());
        userInfo.put("orgName", org != null ? org.getName() : null);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userName", user.getRealName());
        data.put("userInfo", userInfo);
        return Result.ok(data);
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录");
        }
        String token = header.substring(7);
        if (!jwtTokenProvider.validate(token)) {
            throw new BusinessException(401, "登录已失效");
        }
        String username = jwtTokenProvider.parse(token).getSubject();
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        SysOrg org = sysOrgMapper.selectById(user.getOrgId());
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        data.put("orgId", user.getOrgId());
        data.put("orgName", org != null ? org.getName() : null);
        return Result.ok(data);
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }
}
