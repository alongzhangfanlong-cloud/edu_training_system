package com.edu.modules.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.order.entity.OrderItemEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItemEntity> {
}
