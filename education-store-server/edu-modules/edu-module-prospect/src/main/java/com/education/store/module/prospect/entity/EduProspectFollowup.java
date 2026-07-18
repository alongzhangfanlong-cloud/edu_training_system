package com.education.store.module.prospect.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.education.store.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 意向学员跟进记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("edu_prospect_followup")
public class EduProspectFollowup extends BaseEntity {

    /** 意向学员ID → edu_prospect.id */
    private Long prospectId;

    /** 跟进方式(PHONE/WECHAT/FACE_TO_FACE) */
    private String method;

    /** 跟进内容 */
    private String content;

    /** 跟进时间（允许补录历史跟进记录） */
    private LocalDateTime followupDate;

    /** 下次跟进计划 */
    private String nextPlan;

    /** 跟进人ID → sys_user.id */
    private Long operatorId;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
