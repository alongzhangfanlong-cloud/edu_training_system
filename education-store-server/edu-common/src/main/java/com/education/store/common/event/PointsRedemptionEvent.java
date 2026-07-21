package com.education.store.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PointsRedemptionEvent extends ApplicationEvent {
    private final Long pointsRecordId;
    private final Long studentId;
    private final String itemName;
    private final String status;
    private final Integer amount;

    public PointsRedemptionEvent(Object source, Long pointsRecordId, Long studentId,
                                 String itemName, String status, Integer amount) {
        super(source);
        this.pointsRecordId = pointsRecordId;
        this.studentId = studentId;
        this.itemName = itemName;
        this.status = status;
        this.amount = amount;
    }
}
