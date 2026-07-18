package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统参数配置实体
 */
@Data
@TableName("sys_config")
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 参数ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 参数名称 */
    private String name;

    /** 参数键名 */
    @TableField("`key`")
    private String key;

    /** 参数键值 */
    @TableField("`value`")
    private String value;

    /** 系统内置(Y是 N否) */
    private String type;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 备注 */
    private String remark;
}
