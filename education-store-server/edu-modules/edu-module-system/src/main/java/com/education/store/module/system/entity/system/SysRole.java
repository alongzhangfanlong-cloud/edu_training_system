package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色名称 */
    private String name;

    /** 角色编码 */
    private String code;

    /** 排序 */
    private Integer sort;

    /** 菜单树选择是否父子联动 */
    private Integer menuCheckStrictly;

    /** 状态(1正常 0停用) */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 更新者 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /** 菜单ID列表(非数据库字段) */
    @TableField(exist = false)
    private List<Long> menuIds;

    /** 查询用：开始时间(非数据库字段) */
    @TableField(exist = false)
    private LocalDateTime beginTime;

    /** 查询用：结束时间(非数据库字段) */
    @TableField(exist = false)
    private LocalDateTime endTime;
}
