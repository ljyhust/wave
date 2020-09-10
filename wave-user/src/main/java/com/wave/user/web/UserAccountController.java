package com.wave.user.web;

import com.wave.common.PublicResponseDto;
import com.wave.common.PublicResponseObjDto;
import com.wave.common.PublicResponseUtil;
import com.wave.common.RestResult;
import com.wave.exception.WaveException;
import com.wave.user.dto.req.AccountRegistryReqDto;
import com.wave.user.dto.vo.UserIdVo;
import com.wave.user.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("account")
public class UserAccountController {

    @Autowired
    UserAccountService userAccountService;

    /**
     * 注册账号，返回uid
     * @param reqDto
     * @return user id
     * @throws WaveException
     */
    @PostMapping("registry")
    public RestResult registryUser(@Validated AccountRegistryReqDto reqDto) throws WaveException {
        UserIdVo userIdVo = userAccountService.registerUserAccount(reqDto);
        RestResult restResult = PublicResponseUtil.okRestResult(userIdVo);
        return restResult;
    }

    /**
     * 用户登录
     * @param account
     * @return
     * @throws WaveException
     */
    @PostMapping("login/{account}")
    public RestResult userLogin(@PathVariable("account") String account) throws WaveException {
        return null;
    }
}
