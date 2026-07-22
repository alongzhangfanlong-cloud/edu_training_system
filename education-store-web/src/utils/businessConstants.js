/**
 * 业务字典常量 — 替代动态字典加载
 */

/** 学员来源 */
export const STUDENT_SOURCE = [
  { label: '线上推广', value: '1' },
  { label: '线下推广', value: '2' },
  { label: '老带新', value: '3' },
  { label: '其他', value: '4' },
]

/** 意向等级 */
export const INTEREST_LEVEL = [
  { label: '高', value: '1' },
  { label: '中', value: '2' },
  { label: '低', value: '3' },
]

/** 跟进状态 */
export const PROSPECT_FOLLOW_STATUS = [
  { label: '待跟进', value: '1' },
  { label: '已联系', value: '2' },
  { label: '已试听', value: '3' },
  { label: '已报名', value: '4' },
  { label: '已放弃', value: '5' },
]

/** 家长关系 */
export const PARENT_RELATION = [
  { label: '父亲', value: '1' },
  { label: '母亲', value: '2' },
  { label: '其他', value: '3' },
]

/** 学员状态 */
export const STUDENT_STATUS = [
  { label: '在读', value: '1' },
  { label: '结业', value: '2' },
  { label: '停课', value: '3' },
]

/** 班级学员状态 */
export const CLASS_STUDENT_STATUS = [
  { label: '在读', value: '1' },
  { label: '结业', value: '2' },
  { label: '退班', value: '3' },
]

/** 课程类型 */
export const COURSE_TYPE = [
  { label: '班课', value: '1' },
  { label: '一对一', value: '2' },
  { label: '试听课', value: '3' },
]

/** 资源类型 */
export const RESOURCE_TYPE = [
  { label: '课件', value: '1' },
  { label: '视频', value: '2' },
  { label: '文档', value: '3' },
  { label: '其他', value: '4' },
]

// ========== 兼容对象 ==========

export const BUSINESS_DICTS = {
  student_source: STUDENT_SOURCE,
  interest_level: INTEREST_LEVEL,
  prospect_follow_status: PROSPECT_FOLLOW_STATUS,
  parent_relation: PARENT_RELATION,
  student_status: STUDENT_STATUS,
  class_student_status: CLASS_STUDENT_STATUS,
  course_type: COURSE_TYPE,
  resource_type: RESOURCE_TYPE,
}
