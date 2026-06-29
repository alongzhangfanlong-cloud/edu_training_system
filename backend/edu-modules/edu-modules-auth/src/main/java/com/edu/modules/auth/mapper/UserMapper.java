package com.edu.modules.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edu.modules.auth.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    @Select("SELECT r.role_code FROM user_roles ur JOIN roles r ON ur.role_id = r.id WHERE ur.user_id = #{userId}")
    List<String> selectRolesByUserId(@Param("userId") Long userId);

    @Select("SELECT DISTINCT p.perm_code FROM user_roles ur " +
            "JOIN role_permissions rp ON ur.role_id = rp.role_id " +
            "JOIN permissions p ON rp.permission_id = p.id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);

    @Select("SELECT c.id, c.campus_name FROM campuses c WHERE c.principal_id = #{userId} LIMIT 1")
    java.util.Map<String, Object> selectCampusByPrincipal(@Param("userId") Long userId);

    @Select("SELECT c.id, c.campus_name FROM campuses c JOIN teachers t ON t.campus_id = c.id WHERE t.phone = #{phone} LIMIT 1")
    java.util.Map<String, Object> selectCampusByTeacherPhone(@Param("phone") String phone);
}
