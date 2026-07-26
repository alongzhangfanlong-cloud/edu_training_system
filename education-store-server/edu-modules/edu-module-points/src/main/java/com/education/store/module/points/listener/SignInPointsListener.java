package com.education.store.module.points.listener;

import com.education.store.common.event.SignInEvent;
import com.education.store.module.points.service.PointsInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 签到事件监听器 - 签到成功后发放积分奖励
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SignInPointsListener {

    private final PointsInternalService pointsInternalService;

    /**
     * 每次签到奖励5积分
     */
    private static final int SIGN_IN_POINTS = 5;

    @Async
    @EventListener
    public void onSignIn(SignInEvent event) {
        try {
            log.info("签到积分奖励：studentId={}, courseId={}", event.getStudentId(), event.getCourseId());
            pointsInternalService.earnPoints(
                    event.getStudentId(),
                    null,
                    "SIGN_IN",
                    null,
                    SIGN_IN_POINTS,
                    "签到奖励：" + event.getCourseName()
            );
        } catch (Exception e) {
            log.error("签到积分发放失败：studentId={}", event.getStudentId(), e);
        }
    }
}
