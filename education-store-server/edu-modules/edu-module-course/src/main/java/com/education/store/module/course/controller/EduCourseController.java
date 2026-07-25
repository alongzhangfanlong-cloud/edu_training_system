package com.education.store.module.course.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.course.dto.CourseCreateDTO;
import com.education.store.module.course.dto.CourseUpdateDTO;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.entity.EduSubject;
import com.education.store.module.course.mapper.EduSubjectMapper;
import com.education.store.module.course.service.IEduCourseService;
import com.education.store.module.course.vo.CourseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 课程管理控制器
 */
@Tag(name = "课程管理")
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class EduCourseController {

    private final IEduCourseService courseService;
    private final EduSubjectMapper subjectMapper;

    @Operation(summary = "课程列表(分页)")
    @PreAuthorize("@ss.hasPermi('course:course:list')")
    @GetMapping("/list")
    public Result<PageResult<CourseVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) Integer status) {
        Page<EduCourse> p = new Page<>(page, pageSize);
        EduCourse condition = new EduCourse();
        condition.setName(name);
        condition.setCode(code);
        condition.setSubjectId(subjectId);
        condition.setCourseType(courseType);
        condition.setStatus(status);
        IPage<EduCourse> result = courseService.selectCourseList(p, condition);

        // 批量加载科目名称
        Set<Long> subjectIds = result.getRecords().stream()
                .map(EduCourse::getSubjectId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, String> subjectNameMap = loadSubjectNameMap(subjectIds);

        List<CourseVO> records = result.getRecords().stream()
                .map(course -> toVO(course, subjectNameMap))
                .collect(Collectors.toList());
        PageResult<CourseVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(result.getCurrent());
        pageResult.setPageSize(result.getSize());
        return Result.success(pageResult);
    }

    @Operation(summary = "课程详情")
    @PreAuthorize("@ss.hasPermi('course:course:query')")
    @GetMapping("/{id}")
    public Result<CourseVO> detail(@PathVariable Long id) {
        EduCourse course = courseService.selectCourseById(id);
        Map<Long, String> subjectNameMap = loadSubjectNameMap(Set.of(course.getSubjectId()));
        return Result.success(toVO(course, subjectNameMap));
    }

    @Operation(summary = "新增课程")
    @Log(title = "课程管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('course:course:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody CourseCreateDTO dto) {
        EduCourse course = BeanUtil.copyProperties(dto, EduCourse.class);
        if (!courseService.checkCodeUnique(course)) {
            return Result.error("课程编号已存在");
        }
        if (!courseService.checkNameUniqueWithinSubject(course)) {
            return Result.error("同一科目下课程名称已存在");
        }
        courseService.insertCourse(course);
        return Result.success();
    }

    @Operation(summary = "更新课程")
    @Log(title = "课程管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('course:course:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody CourseUpdateDTO dto) {
        if (dto.getId() == null) {
            return Result.badRequest("课程ID不能为空");
        }
        EduCourse course = BeanUtil.copyProperties(dto, EduCourse.class);
        if (!courseService.checkCodeUnique(course)) {
            return Result.error("课程编号已存在");
        }
        if (!courseService.checkNameUniqueWithinSubject(course)) {
            return Result.error("同一科目下课程名称已存在");
        }
        courseService.updateCourse(course);
        return Result.success();
    }

    @Operation(summary = "批量删除课程")
    @Log(title = "课程管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('course:course:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        courseService.deleteCourseByIds(ids);
        return Result.success();
    }

    @Operation(summary = "获取全部已上架课程（下拉选项）")
    @PreAuthorize("@ss.hasPermi('course:course:list')")
    @GetMapping("/all")
    public Result<List<CourseVO>> all() {
        List<EduCourse> list = courseService.selectAllUpCourses();
        Set<Long> subjectIds = list.stream()
                .map(EduCourse::getSubjectId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, String> subjectNameMap = loadSubjectNameMap(subjectIds);
        return Result.success(list.stream().map(c -> toVO(c, subjectNameMap)).collect(Collectors.toList()));
    }

    @Operation(summary = "上架/下架课程")
    @Log(title = "课程管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('course:course:edit')")
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        courseService.updateCourseStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "校验课程编号唯一性")
    @PreAuthorize("@ss.hasPermi('course:course:list')")
    @GetMapping("/check-code")
    public Result<Boolean> checkCode(@RequestParam String code,
                                      @RequestParam(required = false) Long excludeId) {
        EduCourse course = new EduCourse();
        course.setCode(code);
        if (excludeId != null) course.setId(excludeId);
        return Result.success(courseService.checkCodeUnique(course));
    }

    @Operation(summary = "校验同科目下课程名称唯一性")
    @PreAuthorize("@ss.hasPermi('course:course:list')")
    @GetMapping("/check-name")
    public Result<Boolean> checkName(@RequestParam String name,
                                      @RequestParam Long subjectId,
                                      @RequestParam(required = false) Long excludeId) {
        EduCourse course = new EduCourse();
        course.setName(name);
        course.setSubjectId(subjectId);
        if (excludeId != null) course.setId(excludeId);
        return Result.success(courseService.checkNameUniqueWithinSubject(course));
    }

    private CourseVO toVO(EduCourse course, Map<Long, String> subjectNameMap) {
        CourseVO vo = BeanUtil.copyProperties(course, CourseVO.class);
        if (course.getSubjectId() != null) {
            vo.setSubjectName(subjectNameMap.getOrDefault(course.getSubjectId(), null));
        }
        return vo;
    }

    private CourseVO toVO(EduCourse course) {
        return toVO(course, Map.of());
    }

    /** 批量加载科目ID→科目名称映射 */
    private Map<Long, String> loadSubjectNameMap(Set<Long> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return Map.of();
        }
        return subjectMapper.selectBatchIds(subjectIds).stream()
                .collect(Collectors.toMap(EduSubject::getId, EduSubject::getName));
    }
}
