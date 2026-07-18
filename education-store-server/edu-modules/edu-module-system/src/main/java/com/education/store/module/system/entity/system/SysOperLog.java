package com.education.store.module.system.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日志ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作模块 */
    private String title;

    /** 业务类型(INSERT/UPDATE/DELETE/EXPORT等) */
    private String businessType;

    /** 方法名 */
    private String method;

    /** 请求方式(GET/POST/PUT/DELETE) */
    private String requestMethod;

    /** 操作人员 */
    private String operName;

    /** 请求URL */
    private String operUrl;

    /** 操作IP */
    private String operIp;

    /** 操作地点 */
    private String operLocation;

    /** 请求参数(JSON) */
    private String operParam;

    /** 返回结果(JSON) */
    private String jsonResult;

    /** 状态(1成功 0失败) */
    private Integer status;

    /** 错误信息 */
    private String errorMsg;

    /** 耗时(ms) */
    private Long costTime;

    /** 操作时间 */
    private LocalDateTime operTime;
}
