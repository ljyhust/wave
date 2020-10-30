package com.wave.user.web;

import com.wave.common.PublicResponseDto;
import com.wave.common.PublicResponseUtil;
import com.wave.common.RestResult;
import com.wave.exception.WaveException;
import com.wave.user.service.FriendRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户关注  好友
 */
@RestController
@RequestMapping("userFriend")
public class UserFriendRelationController {
    
    @Autowired
    FriendRelationService friendRelationService;
    
    /**
     * 关注好友
     * @param focusUserId 要关注的userId
     * @param userId 登录者
     * @return
     * @throws WaveException
     */
    @PostMapping("focusAdd/{focusUserId}")
    public PublicResponseDto userConcernedAdd(@PathVariable("focusUserId") Long focusUserId,
            @RequestHeader("userId") Long userId) throws WaveException {
        friendRelationService.focusUser(userId, focusUserId);
        return PublicResponseUtil.publicResponseDto();
    }
    
    /**
     * 取消关注
     * @param focusUserId 要取消的userId
     * @param userId 登录者
     * @return
     * @throws WaveException
     */
    @PostMapping("focusDel/{focusUserId}")
    public PublicResponseDto userFocusDelete(@PathVariable("focusUserId") Long focusUserId,
            @RequestHeader("userId") Long userId) throws WaveException {
        friendRelationService.unFocusUser(userId, focusUserId);
        return PublicResponseUtil.publicResponseDto();
    }
}
