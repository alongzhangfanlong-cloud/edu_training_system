package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {

    /** 字典名称 */
    private String name;

    /** 字典编码 */
    private String code;

    /** 状态(1正常 0停用) */
    private Integer status;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 备注 */
    private String remark;
}
