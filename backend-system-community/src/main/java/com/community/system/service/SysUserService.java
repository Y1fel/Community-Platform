package com.community.system.service;

import com.community.common.PageResult;
import com.community.system.dto.UserSaveDTO;
import com.community.system.vo.UserVO;

import java.util.List;

public interface SysUserService {

    PageResult<UserVO> page(int page, int pageSize, String keyword);

    void add(UserSaveDTO dto);

    void update(Long id, UserSaveDTO dto);

    void delete(Long id);

    void batchDelete(List<Long> ids);
}
