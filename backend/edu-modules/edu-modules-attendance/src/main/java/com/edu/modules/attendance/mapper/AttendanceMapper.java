package com.edu.modules.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.attendance.entity.AttendanceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AttendanceMapper extends BaseMapper<AttendanceEntity> {

    @Update("UPDATE attendances SET consume_hours = #{consumeHours} WHERE id = #{id}")
    int updateConsumeHours(@Param("id") Long id, @Param("consumeHours") java.math.BigDecimal consumeHours);
}
