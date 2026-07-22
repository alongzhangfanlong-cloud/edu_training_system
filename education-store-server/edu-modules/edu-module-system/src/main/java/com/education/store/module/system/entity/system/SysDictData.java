package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {

    /** 字典类型编码 */
    private String typeCode;

    /** 字典标签 */
    private String label;

    /** 字典键值 */
    private String value;

    /** 排序 */
    private Integer sort;

    /** 样式属性 */
    private String cssClass;

    /** 表格回显样式 */
    private String listClass;

    /** 是否默认(1是 0否) */
    private Integer isDefault;

    /** 状态(1正常 0停用) */
    private Integer status;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 备注 */
    private String remark;
}
