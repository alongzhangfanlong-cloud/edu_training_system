package com.education.store.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EvaluationSubmittedEvent extends ApplicationEvent {
    private final Long evaluationId;
    private final Long studentId;
    private final String courseName;
    private final String content;
    private final String score;

    public EvaluationSubmittedEvent(Object source, Long evaluationId, Long studentId,
                                    String courseName, String content, String score) {
        super(source);
        this.evaluationId = evaluationId;
        this.studentId = studentId;
        this.courseName = courseName;
        this.content = content;
        this.score = score;
    }
}
