package com.edu.modules.feedback.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class FeedbackRequest {
    @NotNull private Long scheduleId;
    private String content;
    private String homework;
    private List<String> photos;
    private String overallComment;
    private String nextPreview;
    private List<StudentFeedbackItem> studentFeedbacks;

    @Data
    public static class StudentFeedbackItem {
        @NotNull private Long studentId;
        private Integer performanceScore; // 1-5
        private String performanceComment;
        private Integer homeworkStatus; // 1-已完成 2-部分完成 3-未完成
    }
}
