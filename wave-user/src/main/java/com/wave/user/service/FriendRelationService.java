package com.wave.user.service;

import com.wave.common.PageVo;
import com.wave.exception.WaveException;
import com.wave.user.dto.vo.MyConcernUserVo;
import com.wave.user.dto.vo.MyFancyUserVo;

import java.util.List;

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
    
    /**
     * 我的关注
     * @param userId 用户id
     * @param pageIndex 页码
     * @param pageSize 页大小
     * @return
     * @throws WaveException 错误信息
     */
    PageVo myConcernedUsers(Long userId, Integer pageIndex, Integer pageSize) throws WaveException;
    
    /**
     * 我的粉丝-分页
     * @param userId 用户id
     * @param pageIndex 页码
     * @param pageSize 页大小
     * @return
     * @throws WaveException
     */
    PageVo myFancyUsers(Long userId, Integer pageIndex, Integer pageSize) throws WaveException;
    
    /**
     * 查询我的粉丝userId.
     * @param userId
     * @return
     * @throws WaveException
     */
    List<Long> myFancyUserIds(Long userId) throws WaveException;
}
