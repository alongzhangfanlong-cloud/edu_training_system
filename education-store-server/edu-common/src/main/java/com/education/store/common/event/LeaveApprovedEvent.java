package com.education.store.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeaveApprovedEvent extends ApplicationEvent {
    private final Long leaveId;
    private final String role;
    private final Long userId;
    private final String name;
    private final String result;
    private final String reason;

    public LeaveApprovedEvent(Object source, Long leaveId, String role, Long userId,
                              String name, String result, String reason) {
        super(source);
        this.leaveId = leaveId;
        this.role = role;
        this.userId = userId;
        this.name = name;
        this.result = result;
        this.reason = reason;
    }
}
