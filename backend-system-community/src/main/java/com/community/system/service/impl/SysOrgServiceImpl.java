package com.community.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.BusinessException;
import com.community.system.dto.OrgSaveDTO;
import com.community.system.entity.SysOrg;
import com.community.system.entity.SysUser;
import com.community.system.mapper.SysOrgMapper;
import com.community.system.mapper.SysUserMapper;
import com.community.system.service.SysOrgService;
import com.community.system.vo.OrgVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysOrgServiceImpl implements SysOrgService {

    private final SysOrgMapper sysOrgMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public List<OrgVO> tree(String keyword) {
        List<SysOrg> all = sysOrgMapper.selectList(
                new LambdaQueryWrapper<SysOrg>().orderByAsc(SysOrg::getSort).orderByAsc(SysOrg::getId)
        );
        List<OrgVO> tree = buildTree(all);
        if (StringUtils.hasText(keyword)) {
            return filterTree(tree, keyword.trim());
        }
        return tree;
    }

    @Override
    public void add(OrgSaveDTO dto) {
        checkCodeUnique(dto.getCode(), null);
        if (dto.getParentId() != null) {
            SysOrg parent = sysOrgMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("上级组织不存在");
            }
        }
        SysOrg org = new SysOrg();
        org.setParentId(dto.getParentId());
        org.setName(dto.getName());
        org.setCode(dto.getCode());
        org.setSort(dto.getSort() == null ? 0 : dto.getSort());
        sysOrgMapper.insert(org);
    }

    @Override
    public void update(Long id, OrgSaveDTO dto) {
        SysOrg exist = sysOrgMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("组织不存在");
        }
        checkCodeUnique(dto.getCode(), id);

        Long newParentId = dto.getParentId();
        if (newParentId != null) {
            if (Objects.equals(newParentId, id)) {
                throw new BusinessException("不能将组织移动到自身下");
            }
            Set<Long> descendantIds = collectDescendantIds(id);
            if (descendantIds.contains(newParentId)) {
                throw new BusinessException("不能将组织移动到自身子节点下");
            }
            if (sysOrgMapper.selectById(newParentId) == null) {
                throw new BusinessException("上级组织不存在");
            }
        }

        exist.setParentId(newParentId);
        exist.setName(dto.getName());
        exist.setCode(dto.getCode());
        exist.setSort(dto.getSort() == null ? 0 : dto.getSort());
        sysOrgMapper.updateById(exist);
    }

    @Override
    public void delete(Long id) {
        SysOrg exist = sysOrgMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("组织不存在");
        }
        Long childCount = sysOrgMapper.selectCount(
                new LambdaQueryWrapper<SysOrg>().eq(SysOrg::getParentId, id)
        );
        if (childCount != null && childCount > 0) {
            throw new BusinessException("存在子组织，不允许删除");
        }
        Long userCount = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getOrgId, id)
        );
        if (userCount != null && userCount > 0) {
            throw new BusinessException("组织下仍有用户，不允许删除");
        }
        sysOrgMapper.deleteById(id);
    }

    private void checkCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<SysOrg> qw = new LambdaQueryWrapper<SysOrg>().eq(SysOrg::getCode, code);
        if (excludeId != null) {
            qw.ne(SysOrg::getId, excludeId);
        }
        if (sysOrgMapper.selectCount(qw) > 0) {
            throw new BusinessException("组织编码已存在");
        }
    }

    private Set<Long> collectDescendantIds(Long rootId) {
        List<SysOrg> all = sysOrgMapper.selectList(null);
        Map<Long, List<Long>> childrenMap = new HashMap<>();
        for (SysOrg org : all) {
            if (org.getParentId() != null) {
                childrenMap.computeIfAbsent(org.getParentId(), k -> new ArrayList<>()).add(org.getId());
            }
        }
        Set<Long> result = new HashSet<>();
        collect(rootId, childrenMap, result);
        return result;
    }

    private void collect(Long id, Map<Long, List<Long>> childrenMap, Set<Long> result) {
        List<Long> children = childrenMap.getOrDefault(id, List.of());
        for (Long childId : children) {
            result.add(childId);
            collect(childId, childrenMap, result);
        }
    }

    private List<OrgVO> buildTree(List<SysOrg> all) {
        Map<Long, OrgVO> map = new HashMap<>();
        for (SysOrg org : all) {
            OrgVO vo = new OrgVO();
            BeanUtils.copyProperties(org, vo);
            map.put(org.getId(), vo);
        }
        List<OrgVO> roots = new ArrayList<>();
        for (SysOrg org : all) {
            OrgVO vo = map.get(org.getId());
            if (org.getParentId() == null) {
                roots.add(vo);
            } else {
                OrgVO parent = map.get(org.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                } else {
                    roots.add(vo);
                }
            }
        }
        sortTree(roots);
        return roots;
    }

    private void sortTree(List<OrgVO> nodes) {
        nodes.sort(Comparator.comparing(OrgVO::getSort, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(OrgVO::getId));
        for (OrgVO node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortTree(node.getChildren());
            }
        }
    }

    /** 前端 keyword 本地也可搜；后端兼容：保留匹配节点及其祖先/子树 */
    private List<OrgVO> filterTree(List<OrgVO> tree, String keyword) {
        return tree.stream()
                .map(node -> filterNode(node, keyword))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private OrgVO filterNode(OrgVO node, String keyword) {
        List<OrgVO> filteredChildren = node.getChildren().stream()
                .map(c -> filterNode(c, keyword))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        boolean selfMatch = node.getName() != null && node.getName().contains(keyword)
                || node.getCode() != null && node.getCode().contains(keyword);
        if (selfMatch || !filteredChildren.isEmpty()) {
            OrgVO copy = new OrgVO();
            copy.setId(node.getId());
            copy.setParentId(node.getParentId());
            copy.setName(node.getName());
            copy.setCode(node.getCode());
            copy.setSort(node.getSort());
            copy.setChildren(selfMatch ? node.getChildren() : filteredChildren);
            return copy;
        }
        return null;
    }
}
