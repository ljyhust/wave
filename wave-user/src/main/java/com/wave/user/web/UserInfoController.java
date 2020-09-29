package com.wave.user.web;

import com.wave.common.PageVo;
import com.wave.common.PublicResponseDto;
import com.wave.common.PublicResponseObjDto;
import com.wave.common.PublicResponseUtil;
import com.wave.common.RestResult;
import com.wave.exception.WaveException;
import com.wave.user.api.dto.UserInfoDto;
import com.wave.user.dto.req.PageQueryRequestDto;
import com.wave.user.dto.req.UserInfoEditReqDto;
import com.wave.user.dto.req.UserInfoModifyReqDto;
import com.wave.user.dto.req.UserInfoUpdateReqDto;
import com.wave.user.service.FriendRelationService;
import com.wave.user.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserInfoController {

    @Autowired
    UserInfoService userInfoService;
    @Autowired
    FriendRelationService relationService;

    @PostMapping("updateUser")
    public PublicResponseDto updateUserInfo(@Validated UserInfoUpdateReqDto reqDto) throws Exception {
        userInfoService.userInfoUpdate(reqDto);
        return PublicResponseUtil.publicResponseDto();
    }

    /**
     * 根据userId编辑用户信息
     * @return
     * @throws WaveException
     */
    @PostMapping("editUserInfo/{userId}")
    public RestResult editUserInfo(@PathVariable("userId") Long userId, @Validated UserInfoEditReqDto reqDto) throws WaveException {
        // 判断
        if (StringUtils.isBlank(reqDto.getEmail()) && StringUtils.isBlank(reqDto.getMobile())) {
            throw new WaveException(WaveException.INVALID_PARAM, "mobile and email must not be null!");
        }
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(userId);
        userInfoDto.setEmail(reqDto.getEmail());
        userInfoDto.setMobile(reqDto.getMobile());
        userInfoDto.setNickName(reqDto.getNickName());
        userInfoDto.setImageUrl(reqDto.getImgUrl());
        userInfoService.editUserInfo(userInfoDto);
        return PublicResponseUtil.okRestResult();
    }

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping("info/{userId}")
    public PublicResponseObjDto getUserInfo(@PathVariable("userId") String userId) throws Exception {
        UserInfoDto userInfo = userInfoService.getUserInfo(userId);
        return PublicResponseUtil.okPublicResponseObjDto(userInfo);
    }

    /**
     * 根据用户account查询用户信息
     * @param account
     * @return
     * @throws Exception
     */
    @PostMapping("info/account/{account}")
    public PublicResponseObjDto getUserInfoByAccount(@PathVariable("account") String account) throws Exception {
        UserInfoDto userInfoByAccount = userInfoService.getUserInfoByAccount(account);
        if (null == userInfoByAccount.getUserId()) {
            throw new WaveException(WaveException.CLIENT_INVALID_PARAM, "用户未填写信息");
        }
        return PublicResponseUtil.okPublicResponseObjDto(userInfoByAccount);
    }
    
    /**
     * 查询我的关注 - 分页
     * @param userId 我的id
     * @return concernDtos
     * @throws Exception
     */
    @PostMapping("myConcerned/{userId}")
    public PublicResponseObjDto pageConcernedUsers(@PathVariable("userId") Long userId,
            @RequestBody PageQueryRequestDto requestDto) throws Exception {
        PageVo pageVo = relationService.myConcernedUsers(userId, requestDto.getPageIndex(), requestDto.getPageSize());
        return PublicResponseUtil.okPublicResponseObjDto(pageVo);
    }
    
    /**
     * 查询我的粉丝
     * @param userId id
     * @param requestDto 分页信息
     * @return
     * @throws Exception
     */
    @PostMapping("myFancy/{userId}")
    public PublicResponseObjDto pageFancyUsers(@PathVariable("userId") Long userId,
            @RequestBody PageQueryRequestDto requestDto) throws Exception {
        PageVo pageVo = relationService.myFancyUsers(userId, requestDto.getPageIndex(), requestDto.getPageSize());
        return PublicResponseUtil.okPublicResponseObjDto(pageVo);
    }
}
