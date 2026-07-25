package com.education.store.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.dto.LoginDTO;
import com.education.store.module.system.dto.LoginVO;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.vo.UserSimpleVO;

import java.util.List;
import java.util.Set;

/**
 * 用户 Service
 */
public interface SysUserService extends IService<SysUser> {

    /** 用户名密码登录（认证通过后生成Token） */
    LoginVO login(LoginDTO loginDTO);

    /** 微信小程序静默登录（code → openid → JWT） */
    LoginVO wechatLogin(String code);

    /** 微信小程序手机号绑定登录（code + 加密手机号 → 解密 → 绑定 → JWT） */
    LoginVO wechatBind(String code, String encryptedPhone, String iv);

    /** 刷新Token（用RefreshToken换取新的AccessToken+RefreshToken） */
    LoginVO refreshToken(String refreshToken);

    /** 根据用户名查询用户 */
    SysUser getByUsername(String username);

    /** 根据微信OpenID查询用户 */
    SysUser getByOpenid(String openid);

    /** 根据手机号查询用户 */
    SysUser getByMobile(String mobile);

    /** 绑定微信OpenID到用户 */
    void bindOpenid(Long userId, String openid);

    /** 分页查询用户列表 */
    Page<SysUser> getUserPage(long page, long size, String keyword, Integer status);

    /** 修改密码 */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /** 获取用户权限标识 */
    Set<String> getUserPermissions(Long userId);

    // ======== 系统管理 CRUD 方法 ========

    /** 分页查询用户列表（管理系统用） */
    IPage<SysUser> selectUserList(IPage<SysUser> page, SysUser user);

    /** 根据用户ID查询用户 */
    SysUser selectUserById(Long userId);

    /** 根据用户名查询用户（管理系统用） */
    SysUser selectUserByUsername(String username);

    /** 新增用户（保存用户+角色） */
    int insertUser(SysUser user);

    /** 更新用户（更新用户+角色，密码为空则不修改） */
    int updateUser(SysUser user);

    /** 根据ID数组批量软删除用户 */
    int deleteUserByIds(Long[] userIds);

    /** 重置密码 */
    int resetPwd(SysUser user);

    /** 更新用户状态 */
    int updateUserStatus(SysUser user);

    /** 导入用户 */
    String importUser(List<SysUser> list, boolean updateSupport, String operName);

    /** 校验用户名是否唯一 */
    boolean checkUserNameUnique(SysUser user);

    /** 校验手机号是否唯一 */
    boolean checkPhoneUnique(SysUser user);

    /** 校验邮箱是否唯一 */
    boolean checkEmailUnique(SysUser user);

    /** 校验用户是否允许操作（不允许操作admin） */
    void checkUserAllowed(SysUser user);

    /** 新增用户角色关联 */
    void insertUserAuth(Long userId, Long[] roleIds);

    /** 查询用户角色ID列表 */
    List<Long> selectUserRoleIds(Long userId);

    /** 导出用户（按查询条件返回 Excel 字节数组） */
    byte[] exportUser(String keyword, Integer status);

    /** 查询拥有指定角色的所有启用用户（用于下拉选项） */
    List<UserSimpleVO> listUsersByRoleCode(String roleCode);
}
