package com.wave.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wave.common.WaveConstants;
import com.wave.exception.WaveException;
import com.wave.user.dao.UserAccountDao;
import com.wave.user.dao.entity.AccountEntity;
import com.wave.user.dto.req.AccountRegistryReqDto;
import com.wave.user.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    UserAccountDao userAccountDao;

    @Override
    public void registerUserAccount(AccountRegistryReqDto reqDto) throws WaveException {
        // 查询账号是否存在
        QueryWrapper<AccountEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", reqDto.getAccount());
        queryWrapper.eq("status", 0);
        List<AccountEntity> entities = userAccountDao.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(entities)) {
            throw new WaveException(WaveException.INVALID_PARAM, "账号已存在");
        }
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccount(reqDto.getAccount());
        accountEntity.setType(reqDto.getType());
        userAccountDao.insert(accountEntity);
        log.info("====> account.hashCode % 5 = {}", reqDto.getAccount().hashCode() % 5);
    }
}
