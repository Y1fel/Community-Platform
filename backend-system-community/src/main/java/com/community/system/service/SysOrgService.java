package com.community.system.service;

import com.community.system.dto.OrgSaveDTO;
import com.community.system.vo.OrgVO;

import java.util.List;

public interface SysOrgService {

    List<OrgVO> tree(String keyword);

    void add(OrgSaveDTO dto);

    void update(Long id, OrgSaveDTO dto);

    void delete(Long id);
}
