package com.wave.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wave.common.WaveConstants;
import com.wave.exception.WaveException;
import com.wave.user.dao.UserAccountDao;
import com.wave.user.dao.UserInfoDao;
import com.wave.user.dao.entity.AccountEntity;
import com.wave.user.dao.entity.UserInfoEntity;
import com.wave.user.dto.req.AccountRegistryReqDto;
import com.wave.user.dto.vo.UserIdVo;
import com.wave.user.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final String REDIS_LOCK_ACCOUNT_EDIT_PREFIX = "LOCK:ACCOUNT:";

    @Autowired
    UserAccountDao userAccountDao;

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 注册账号：
     * 1. 不存在，则先添加t_user_info 再添加 t_account
     * 2. 存在，判断uid != NULL ? return uid : insert user && update account
     * 考虑加锁 lock:account:xxxxx
     * @param reqDto
     * @return
     * @throws WaveException
     */
    @Override
    public UserIdVo registerUserAccount(AccountRegistryReqDto reqDto) throws WaveException {
        RLock accountLock = redissonClient.getLock(REDIS_LOCK_ACCOUNT_EDIT_PREFIX + reqDto.getAccount());
        try {
            boolean b = accountLock.tryLock(1000, 1000, TimeUnit.MILLISECONDS);
            if (!b) {
                throw new WaveException(WaveException.SERVER_ERROR, "服务忙");
            }
            // 查询账号是否存在
            QueryWrapper<AccountEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account", reqDto.getAccount());
            queryWrapper.eq("status", WaveConstants.NORMAL_STATUS);
            List<AccountEntity> entities = userAccountDao.selectList(queryWrapper);
            UserIdVo userIdVo = new UserIdVo();
            if (CollectionUtils.isEmpty(entities)) {
                // 添加t_user_info
                UserInfoEntity userInfo = new UserInfoEntity();
                userInfo.setNickName(reqDto.getAccount());
                wrapUserAccount(reqDto, userInfo);
                userInfoDao.insert(userInfo);

                AccountEntity accountEntity = new AccountEntity();
                accountEntity.setAccount(reqDto.getAccount());
                accountEntity.setType(reqDto.getType());
                accountEntity.setUserId(userInfo.getId());
                userAccountDao.insert(accountEntity);

                // 入缓存 account_user_id_key？
                userIdVo.setUserId(userInfo.getId());
                return userIdVo;
            }

            // FIXME 重复account是否有问题？
            // 重复多个account ? 取uid不为空的一个，如果没有则取第一个
            for (AccountEntity accountEntity : entities) {
                if (null != accountEntity.getUserId()) {
                    throw new WaveException(209, "账号已经被注册");
                /*userIdVo.setUserId(accountEntity.getUserId());
                return userIdVo;*/
                }
            }

            // 不存在uid
            UserInfoEntity userInfo = new UserInfoEntity();
            userInfo.setNickName(reqDto.getAccount());
            wrapUserAccount(reqDto, userInfo);
            userInfoDao.insert(userInfo);

            AccountEntity updateAccount = new AccountEntity();
            updateAccount.setId(entities.get(0).getId());
            updateAccount.setUserId(userInfo.getId());
            userAccountDao.updateById(updateAccount);

            log.info("====> account.hashCode % 5 = {}", reqDto.getAccount().hashCode() % 5);
            userIdVo.setUserId(userInfo.getId());
            return userIdVo;
        } catch (InterruptedException e) {
            log.info("=====> lock account {} error {}", reqDto.getAccount(), e);
            throw new WaveException(WaveException.SERVER_ERROR, "服务忙");
        } finally {
            if (null != accountLock) {
                accountLock.forceUnlock();
            }
        }
    }

    public void wrapUserAccount(AccountRegistryReqDto reqDto, UserInfoEntity userInfoEntity) {
        switch (reqDto.getType()) {
            case 1 : userInfoEntity.setMobile(reqDto.getAccount()); break;
            case 2 : userInfoEntity.setEmail(reqDto.getAccount()); break;
            default: break;
        }
    }
}
