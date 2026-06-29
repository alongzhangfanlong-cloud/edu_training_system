package com.edu.modules.student.spi;

import com.edu.modules.student.dto.StudentDetailDTO;

/**
 * 学员模块对外SPI接口
 * 其他模块只能通过此接口访问学员数据，不能直接访问Mapper
 */
public interface StudentExternalService {

    /**
     * 获取学员基本信息
     */
    StudentDetailDTO getStudentDetail(Long studentId);

    /**
     * 检查学员是否存在
     */
    boolean existsById(Long studentId);
}
