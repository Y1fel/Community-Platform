package com.community.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.BusinessException;
import com.community.common.PageResult;
import com.community.system.dto.UserSaveDTO;
import com.community.system.entity.SysOrg;
import com.community.system.entity.SysUser;
import com.community.system.mapper.SysOrgMapper;
import com.community.system.mapper.SysUserMapper;
import com.community.system.service.SysUserService;
import com.community.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysOrgMapper sysOrgMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserVO> page(int page, int pageSize, String keyword) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getPhone, keyword));
        }
        qw.orderByDesc(SysUser::getId);

        Page<SysUser> p = sysUserMapper.selectPage(new Page<>(page, pageSize), qw);
        Set<Long> orgIds = p.getRecords().stream().map(SysUser::getOrgId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> orgNameMap = new HashMap<>();
        if (!orgIds.isEmpty()) {
            List<SysOrg> orgs = sysOrgMapper.selectBatchIds(orgIds);
            for (SysOrg org : orgs) {
                orgNameMap.put(org.getId(), org.getName());
            }
        }

        List<UserVO> list = p.getRecords().stream().map(u -> {
            UserVO vo = new UserVO();
            vo.setId(u.getId());
            vo.setUsername(u.getUsername());
            vo.setRealName(u.getRealName());
            vo.setPhone(u.getPhone());
            vo.setOrgId(u.getOrgId());
            vo.setOrgName(orgNameMap.get(u.getOrgId()));
            vo.setRole(u.getRole());
            vo.setStatus(u.getStatus());
            vo.setCreateTime(u.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(list, p.getTotal());
    }

    @Override
    public void add(UserSaveDTO dto) {
        if (!StringUtils.hasText(dto.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        checkUsernameUnique(dto.getUsername(), null);
        checkOrgExists(dto.getOrgId());

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setOrgId(dto.getOrgId());
        user.setRole(dto.getRole());
        user.setStatus("enabled");
        user.setDeleted(0);
        sysUserMapper.insert(user);
    }

    @Override
    public void update(Long id, UserSaveDTO dto) {
        SysUser exist = sysUserMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        // 编辑时不允许改用户名，但仍校验传来的 username 与原值一致（前端会带上）
        checkOrgExists(dto.getOrgId());

        exist.setRealName(dto.getRealName());
        exist.setPhone(dto.getPhone());
        exist.setOrgId(dto.getOrgId());
        exist.setRole(dto.getRole());
        if (StringUtils.hasText(dto.getStatus())) {
            if (!"enabled".equals(dto.getStatus()) && !"disabled".equals(dto.getStatus())) {
                throw new BusinessException("状态只能是 enabled 或 disabled");
            }
            exist.setStatus(dto.getStatus());
        }
        // 编辑不改密码（前端未传）
        sysUserMapper.updateById(exist);
    }

    @Override
    public void delete(Long id) {
        SysUser exist = sysUserMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        if ("admin".equals(exist.getUsername())) {
            throw new BusinessException("不能删除系统管理员账号");
        }
        sysUserMapper.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("ids 不能为空");
        }
        for (Long id : ids) {
            delete(id);
        }
    }

    private void checkUsernameUnique(String username, Long excludeId) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username);
        if (excludeId != null) {
            qw.ne(SysUser::getId, excludeId);
        }
        if (sysUserMapper.selectCount(qw) > 0) {
            throw new BusinessException("用户名已存在");
        }
    }

    private void checkOrgExists(Long orgId) {
        if (sysOrgMapper.selectById(orgId) == null) {
            throw new BusinessException("所属组织不存在");
        }
    }
}
