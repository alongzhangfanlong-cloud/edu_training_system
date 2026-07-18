package com.education.store.module.student.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.student.dto.StudentCreateDTO;
import com.education.store.module.student.dto.StudentUpdateDTO;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.service.IEduStudentService;
import com.education.store.module.student.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 在学学员管理控制器
 */
@Tag(name = "学员管理")
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class EduStudentController {

    private final IEduStudentService studentService;

    @Operation(summary = "学员列表(分页)")
    @PreAuthorize("@ss.hasPermi('student:enrolled:list')")
    @GetMapping("/list")
    public Result<PageResult<StudentVO>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long counselorId,
            @RequestParam(required = false) String source) {
        Page<EduStudent> p = new Page<>(page, pageSize);
        EduStudent condition = new EduStudent();
        // keyword 同时模糊匹配姓名和手机号
        if (keyword != null && !keyword.isBlank()) {
            condition.setName(keyword);
            condition.setMobile(keyword);
        } else {
            condition.setName(name);
            condition.setMobile(mobile);
        }
        condition.setGrade(grade);
        condition.setStatus(status);
        condition.setCounselorId(counselorId);
        condition.setSource(source);
        IPage<EduStudent> result = studentService.selectStudentList(p, condition);
        List<StudentVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        PageResult<StudentVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(result.getCurrent());
        pageResult.setPageSize(result.getSize());
        return Result.success(pageResult);
    }

    @Operation(summary = "学员详情")
    @PreAuthorize("@ss.hasPermi('student:enrolled:query')")
    @GetMapping("/{id}")
    public Result<StudentVO> detail(@PathVariable Long id) {
        EduStudent student = studentService.selectStudentById(id);
        return Result.success(toVO(student));
    }

    @Operation(summary = "新增学员")
    @Log(title = "学员管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('student:enrolled:add')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody StudentCreateDTO dto) {
        EduStudent student = BeanUtil.copyProperties(dto, EduStudent.class);
        if (!studentService.checkMobileUnique(student)) {
            return Result.error("手机号已被其他学员使用");
        }
        studentService.insertStudent(student);
        return Result.success();
    }

    @Operation(summary = "更新学员信息")
    @Log(title = "学员管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('student:enrolled:edit')")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody StudentUpdateDTO dto) {
        if (dto.getId() == null) {
            return Result.badRequest("学员ID不能为空");
        }
        EduStudent student = BeanUtil.copyProperties(dto, EduStudent.class);
        if (!studentService.checkMobileUnique(student)) {
            return Result.error("手机号已被其他学员使用");
        }
        studentService.updateStudent(student);
        return Result.success();
    }

    @Operation(summary = "批量删除学员")
    @Log(title = "学员管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('student:enrolled:delete')")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable Long[] ids) {
        studentService.deleteStudentByIds(ids);
        return Result.success();
    }

    @Operation(summary = "更新学员状态")
    @Log(title = "学员管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('student:enrolled:edit')")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        studentService.updateStudentStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "学员简列表（下拉搜索用）")
    @PreAuthorize("@ss.hasPermi('student:enrolled:list')")
    @GetMapping("/simple")
    public Result<List<StudentVO>> simpleList(@RequestParam(required = false) String keyword) {
        List<EduStudent> list = studentService.selectStudentSimpleList(keyword);
        return Result.success(list.stream().map(this::toVO).collect(Collectors.toList()));
    }

    @Operation(summary = "手机号唯一性校验")
    @PreAuthorize("@ss.hasPermi('student:enrolled:list')")
    @GetMapping("/check-mobile")
    public Result<Boolean> checkMobile(@RequestParam String mobile,
                                       @RequestParam(required = false) Long excludeId) {
        EduStudent check = new EduStudent();
        check.setMobile(mobile);
        if (excludeId != null) {
            check.setId(excludeId);
        }
        return Result.success(studentService.checkMobileUnique(check));
    }

    @Operation(summary = "批量更新学员状态")
    @Log(title = "学员管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('student:enrolled:edit')")
    @PutMapping("/batch/status")
    public Result<Void> batchUpdateStatus(@RequestBody java.util.Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> idList = (List<Integer>) body.get("ids");
        String status = (String) body.get("status");
        if (idList == null || idList.isEmpty() || status == null) {
            return Result.badRequest("ids和status不能为空");
        }
        Long[] ids = idList.stream().map(Integer::longValue).toArray(Long[]::new);
        studentService.batchUpdateStatus(ids, status);
        return Result.success();
    }

    @Operation(summary = "批量更改顾问")
    @Log(title = "学员管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('student:enrolled:edit')")
    @PutMapping("/batch/consultant")
    public Result<Void> batchUpdateConsultant(@RequestBody java.util.Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> idList = (List<Integer>) body.get("ids");
        Integer consultantId = (Integer) body.get("consultantId");
        if (idList == null || idList.isEmpty() || consultantId == null) {
            return Result.badRequest("ids和consultantId不能为空");
        }
        Long[] ids = idList.stream().map(Integer::longValue).toArray(Long[]::new);
        studentService.batchUpdateConsultant(ids, consultantId.longValue());
        return Result.success();
    }

    private StudentVO toVO(EduStudent student) {
        return BeanUtil.copyProperties(student, StudentVO.class);
    }
}
