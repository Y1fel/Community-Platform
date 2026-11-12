package com.community.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.BusinessException;
import com.community.common.PageResult;
import com.community.system.dto.DictSaveDTO;
import com.community.system.entity.SysDict;
import com.community.system.mapper.SysDictMapper;
import com.community.system.service.SysDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysDictServiceImpl implements SysDictService {

    private final SysDictMapper sysDictMapper;

    @Override
    public PageResult<SysDict> page(int page, int pageSize, String keyword) {
        LambdaQueryWrapper<SysDict> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(SysDict::getDictType, keyword)
                    .or()
                    .like(SysDict::getDictName, keyword));
        }
        qw.orderByAsc(SysDict::getSort).orderByDesc(SysDict::getId);
        Page<SysDict> p = sysDictMapper.selectPage(new Page<>(page, pageSize), qw);
        return new PageResult<>(p.getRecords(), p.getTotal());
    }

    @Override
    public void add(DictSaveDTO dto) {
        checkUnique(dto.getDictType(), dto.getDictCode(), null);
        SysDict dict = new SysDict();
        dict.setDictType(dto.getDictType());
        dict.setDictName(dto.getDictName());
        dict.setDictCode(dto.getDictCode());
        dict.setSort(dto.getSort() == null ? 0 : dto.getSort());
        dict.setRemark(dto.getRemark());
        sysDictMapper.insert(dict);
    }

    @Override
    public void update(Long id, DictSaveDTO dto) {
        SysDict exist = sysDictMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("字典不存在");
        }
        checkUnique(dto.getDictType(), dto.getDictCode(), id);
        exist.setDictType(dto.getDictType());
        exist.setDictName(dto.getDictName());
        exist.setDictCode(dto.getDictCode());
        exist.setSort(dto.getSort() == null ? 0 : dto.getSort());
        exist.setRemark(dto.getRemark());
        sysDictMapper.updateById(exist);
    }

    @Override
    public void delete(Long id) {
        if (sysDictMapper.selectById(id) == null) {
            throw new BusinessException("字典不存在");
        }
        sysDictMapper.deleteById(id);
    }

    private void checkUnique(String type, String code, Long excludeId) {
        LambdaQueryWrapper<SysDict> qw = new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictType, type)
                .eq(SysDict::getDictCode, code);
        if (excludeId != null) {
            qw.ne(SysDict::getId, excludeId);
        }
        if (sysDictMapper.selectCount(qw) > 0) {
            throw new BusinessException("同一字典类型下编码已存在");
        }
    }
}
