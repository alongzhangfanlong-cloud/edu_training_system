package com.education.store.module.classinfo.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.classinfo.dto.ClassCreateDTO;
import com.education.store.module.classinfo.dto.ClassUpdateDTO;
import com.education.store.module.classinfo.entity.EduClass;
import com.education.store.module.classinfo.service.IEduClassService;
import com.education.store.module.classinfo.vo.ClassVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 班级管理控制器
 */
@Tag(name = "班级管理")
@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class EduClassController {

    private final IEduClassService classService;

    @Operation(summary = "班级列表(分页)")
    @PreAuthorize("@ss.hasPermi('class:class:list')")
    @GetMapping("/list")
    public Result<PageResult<ClassVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String status) {
        Page<EduClass> p = new Page<>(page, pageSize);
        EduClass condition = new EduClass();
        condition.setName(name);
        condition.setCode(code);
        condition.setCourseId(courseId);
        condition.setTeacherId(teacherId);
        condition.setStatus(status);
        IPage<EduClass> result = classService.selectClassList(p, condition);
        List<ClassVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        PageResult<ClassVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(result.getCurrent());
        pageResult.setPageSize(result.getSize());
        return Result.success(pageResult);
    }

    @Operation(summary = "班级详情")
    @PreAuthorize("@ss.hasPermi('class:class:query')")
    @GetMapping("/{id}")
    public Result<ClassVO> detail(@PathVariable Long id) {
        EduClass clazz = classService.selectClassById(id);
        return Result.success(toVO(clazz));
    }

    @Operation(summary = "新增班级（自动生成编号 + 同步创建排课规则）")
    @Log(title = "班级管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('class:class:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ClassCreateDTO dto) {
        EduClass clazz = BeanUtil.copyProperties(dto, EduClass.class);
        classService.insertClass(clazz);
        return Result.success();
    }

    @Operation(summary = "更新班级信息")
    @Log(title = "班级管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('class:class:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody ClassUpdateDTO dto) {
        if (dto.getId() == null) {
            return Result.badRequest("班级ID不能为空");
        }
        EduClass clazz = BeanUtil.copyProperties(dto, EduClass.class);
        classService.updateClass(clazz);
        return Result.success();
    }

    @Operation(summary = "批量删除班级")
    @Log(title = "班级管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('class:class:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        classService.deleteClassByIds(ids);
        return Result.success();
    }

    @Operation(summary = "更新班级状态")
    @Log(title = "班级管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('class:class:edit')")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        classService.updateClassStatus(id, status);
        return Result.success();
    }

    private ClassVO toVO(EduClass clazz) {
        return BeanUtil.copyProperties(clazz, ClassVO.class);
    }
}
