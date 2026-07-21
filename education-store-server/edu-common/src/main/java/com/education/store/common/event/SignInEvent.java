package com.education.store.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.time.LocalDateTime;

@Getter
public class SignInEvent extends ApplicationEvent {
    private final Long studentId;
    private final Long courseId;
    private final String courseName;
    private final LocalDateTime signinTime;

    public SignInEvent(Object source, Long studentId, Long courseId,
                       String courseName, LocalDateTime signinTime) {
        super(source);
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.signinTime = signinTime;
    }
}
