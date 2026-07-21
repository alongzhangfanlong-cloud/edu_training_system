package com.education.store.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.time.LocalDate;

@Getter
public class HomeworkPublishedEvent extends ApplicationEvent {
    private final Long homeworkId;
    private final Long classId;
    private final String className;
    private final String title;
    private final LocalDate deadline;

    public HomeworkPublishedEvent(Object source, Long homeworkId, Long classId,
                                  String className, String title, LocalDate deadline) {
        super(source);
        this.homeworkId = homeworkId;
        this.classId = classId;
        this.className = className;
        this.title = title;
        this.deadline = deadline;
    }
}
