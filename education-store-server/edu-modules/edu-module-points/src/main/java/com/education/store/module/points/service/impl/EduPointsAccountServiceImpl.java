package com.education.store.module.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.points.entity.EduPointsAccount;
import com.education.store.module.points.mapper.EduPointsAccountMapper;
import com.education.store.module.points.service.IEduPointsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 积分账户 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduPointsAccountServiceImpl extends ServiceImpl<EduPointsAccountMapper, EduPointsAccount>
        implements IEduPointsAccountService {

    private final EduPointsAccountMapper accountMapper;

    @Override
    public EduPointsAccount getByStudentId(Long studentId) {
        LambdaQueryWrapper<EduPointsAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduPointsAccount::getStudentId, studentId);
        return accountMapper.selectOne(wrapper);
    }

    @Override
    public EduPointsAccount initAccount(Long studentId) {
        EduPointsAccount existing = getByStudentId(studentId);
        if (existing != null) {
            return existing;
        }
        EduPointsAccount account = new EduPointsAccount();
        account.setStudentId(studentId);
        account.setTotalEarned(0);
        account.setTotalSpent(0);
        account.setBalance(0);
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        accountMapper.insert(account);
        return account;
    }
}
