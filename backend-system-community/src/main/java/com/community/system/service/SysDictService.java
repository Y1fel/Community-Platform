package com.community.system.service;

import com.community.common.PageResult;
import com.community.system.dto.DictSaveDTO;
import com.community.system.entity.SysDict;

public interface SysDictService {

    PageResult<SysDict> page(int page, int pageSize, String keyword);

    void add(DictSaveDTO dto);

    void update(Long id, DictSaveDTO dto);

    void delete(Long id);
}
