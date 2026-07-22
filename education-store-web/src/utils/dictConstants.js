/**
 * 系统字典常量 — 替代动态字典加载，所有字典值硬编码于此
 *
 * 与旧 dicts store 兼容：HARDCODED_DICTS 模拟 store.dicts 结构，
 * 可直接用 `dicts.sys_user_sex` 方式访问。
 */

// ========== 系统级字典 ==========

/** 用户性别 */
export const USER_SEX = [
  { label: '男', value: 1 },
  { label: '女', value: 2 },
  { label: '未知', value: 0 },
]

/** 系统开关（正常/停用） */
export const NORMAL_DISABLE = [
  { label: '正常', value: 1 },
  { label: '停用', value: 0 },
]

/** 系统是否 */
export const YES_NO = [
  { label: '是', value: 'Y' },
  { label: '否', value: 'N' },
]

/** 通知类型 */
export const NOTICE_TYPE = [
  { label: '通知', value: '1' },
  { label: '公告', value: '2' },
]

/** 菜单类型 */
export const MENU_TYPE = [
  { label: '目录', value: 'M' },
  { label: '菜单', value: 'C' },
  { label: '按钮', value: 'F' },
]

/** 显隐 */
export const SHOW_HIDE = [
  { label: '显示', value: 1 },
  { label: '隐藏', value: 0 },
]

// ========== 兼容对象（模拟 store.dicts 的访问结构） ==========

/** 硬编码字典汇总，key 为旧字典码 */
export const HARDCODED_DICTS = {
  sys_user_sex: USER_SEX,
  sys_normal_disable: NORMAL_DISABLE,
  sys_yes_no: YES_NO,
  sys_notice_type: NOTICE_TYPE,
  sys_menu_type: MENU_TYPE,
  sys_show_hide: SHOW_HIDE,
}
