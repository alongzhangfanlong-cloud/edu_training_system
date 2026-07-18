package com.education.store.module.course.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.course.dto.SubjectCreateDTO;
import com.education.store.module.course.dto.SubjectUpdateDTO;
import com.education.store.module.course.entity.EduSubject;
import com.education.store.module.course.service.IEduSubjectService;
import com.education.store.module.course.vo.SubjectVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 科目管理控制器
 */
@Tag(name = "科目管理")
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class EduSubjectController {

    private final IEduSubjectService subjectService;

    @Operation(summary = "科目列表(分页)")
    @PreAuthorize("@ss.hasPermi('course:subject:list')")
    @GetMapping("/list")
    public Result<PageResult<SubjectVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        Page<EduSubject> p = new Page<>(page, pageSize);
        EduSubject condition = new EduSubject();
        condition.setName(name);
        condition.setStatus(status);
        IPage<EduSubject> result = subjectService.selectSubjectList(p, condition);
        List<SubjectVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        PageResult<SubjectVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(result.getCurrent());
        pageResult.setPageSize(result.getSize());
        return Result.success(pageResult);
    }

    @Operation(summary = "科目详情")
    @PreAuthorize("@ss.hasPermi('course:subject:query')")
    @GetMapping("/{id}")
    public Result<SubjectVO> detail(@PathVariable Long id) {
        EduSubject subject = subjectService.selectSubjectById(id);
        return Result.success(toVO(subject));
    }

    @Operation(summary = "新增科目")
    @Log(title = "科目管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('course:subject:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SubjectCreateDTO dto) {
        EduSubject subject = BeanUtil.copyProperties(dto, EduSubject.class);
        if (!subjectService.checkNameUnique(subject)) {
            return Result.error("科目名称已存在");
        }
        subjectService.insertSubject(subject);
        return Result.success();
    }

    @Operation(summary = "更新科目")
    @Log(title = "科目管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('course:subject:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SubjectUpdateDTO dto) {
        if (dto.getId() == null) {
            return Result.badRequest("科目ID不能为空");
        }
        EduSubject subject = BeanUtil.copyProperties(dto, EduSubject.class);
        if (!subjectService.checkNameUnique(subject)) {
            return Result.error("科目名称已存在");
        }
        subjectService.updateSubject(subject);
        return Result.success();
    }

    @Operation(summary = "批量删除科目")
    @Log(title = "科目管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('course:subject:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        subjectService.deleteSubjectByIds(ids);
        return Result.success();
    }

    private SubjectVO toVO(EduSubject subject) {
        return BeanUtil.copyProperties(subject, SubjectVO.class);
    }
}
