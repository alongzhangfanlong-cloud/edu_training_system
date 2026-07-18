package com.education.store.module.teacher.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 教师待办 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherTodosVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 待批改作业份数 */
    private Integer pendingGradingCount;

    /** 待回复请假条数 */
    private Integer pendingLeaveReplyCount;
}
