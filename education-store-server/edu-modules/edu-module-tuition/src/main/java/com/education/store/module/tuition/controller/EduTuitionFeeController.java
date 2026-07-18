package com.education.store.module.tuition.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.store.common.annotation.Log;
import com.education.store.common.enums.BusinessType;
import com.education.store.common.model.PageResult;
import com.education.store.common.model.Result;
import com.education.store.module.tuition.dto.FeeCreateDTO;
import com.education.store.module.tuition.dto.FeeUpdateDTO;
import com.education.store.module.tuition.entity.EduTuitionFee;
import com.education.store.module.tuition.service.IEduTuitionFeeService;
import com.education.store.module.tuition.vo.FeeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "收费管理")
@RestController
@RequestMapping("/api/v1/tuition/fees")
@RequiredArgsConstructor
public class EduTuitionFeeController {

    private final IEduTuitionFeeService feeService;

    @Operation(summary = "收费记录列表")
    @PreAuthorize("@ss.hasPermi('tuition:fee:list')")
    @GetMapping("/list")
    public Result<PageResult<FeeVO>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize, @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long enrollmentId, @RequestParam(required = false) String status) {
        Page<EduTuitionFee> p = new Page<>(page, pageSize);
        EduTuitionFee condition = new EduTuitionFee();
        condition.setStudentId(studentId); condition.setEnrollmentId(enrollmentId); condition.setStatus(status);
        IPage<EduTuitionFee> result = feeService.selectFeeList(p, condition);
        List<FeeVO> records = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        PageResult<FeeVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(result.getCurrent());
        pageResult.setPageSize(result.getSize());
        return Result.success(pageResult);
    }

    @Operation(summary = "收费记录详情")
    @GetMapping("/{id}") @PreAuthorize("@ss.hasPermi('tuition:fee:list')")
    public Result<FeeVO> detail(@PathVariable Long id) {
        return Result.success(toVO(feeService.selectFeeById(id)));
    }

    @Operation(summary = "新增收费记录")
    @PostMapping @Log(title = "收费管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('tuition:fee:add')")
    public Result<Void> create(@Valid @RequestBody FeeCreateDTO dto) {
        EduTuitionFee fee = BeanUtil.copyProperties(dto, EduTuitionFee.class);
        feeService.insertFee(fee);
        return Result.success();
    }

    @Operation(summary = "修改收费记录")
    @PutMapping @Log(title = "收费管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('tuition:fee:edit')")
    public Result<Void> update(@Valid @RequestBody FeeUpdateDTO dto) {
        EduTuitionFee fee = BeanUtil.copyProperties(dto, EduTuitionFee.class);
        feeService.updateFee(fee);
        return Result.success();
    }

    @Operation(summary = "删除收费记录")
    @DeleteMapping("/{ids}") @Log(title = "收费管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('tuition:fee:delete')")
    public Result<Void> delete(@PathVariable Long[] ids) {
        feeService.deleteFeeByIds(ids);
        return Result.success();
    }

    private FeeVO toVO(EduTuitionFee fee) {
        return BeanUtil.copyProperties(fee, FeeVO.class);
    }
}
