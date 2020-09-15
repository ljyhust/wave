package com.wave.user.service;

import com.wave.exception.WaveException;
import com.wave.user.dto.vo.MyConcernUserVo;
import com.wave.user.dto.vo.MyFancyUserVo;

/**
 * 好友关系业务类
 * 我的关注
 * 我的粉丝
 */
public interface FriendRelationService {

    // 查询我的关注
    MyConcernUserVo queryMyConcernUserInfo(Long userId) throws WaveException;

    // 查询我的粉丝
    MyFancyUserVo queryMyFancyUserInfo(Long userId) throws WaveException;

    /**
     * 关注某个用户
     * @param userId 登录者id
     * @param focusUserId 要关注的用户id
     * @throws WaveException
     */
    void focusUser(Long userId, Long focusUserId) throws WaveException;

    /**
     * 取消某人关注
     * @param userId 登录者id
     * @param focusUsersId 要取消的用户id
     * @throws WaveException
     */
    void unFocusUser(Long userId, Long focusUserId) throws WaveException;
}
