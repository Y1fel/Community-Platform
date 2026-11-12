package com.community.system.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrgVO {

    private Long id;
    private Long parentId;
    private String name;
    private String code;
    private Integer sort;
    private List<OrgVO> children = new ArrayList<>();
}
