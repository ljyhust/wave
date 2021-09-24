package com.play.controller;

import com.play.dto.PlayerInputVo;
import com.play.service.PlayServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zz
 * @date 2021/8/30 18:26
 */
@RequestMapping("player")
@RestController
public class PlayerController {

    @Autowired
    PlayServiceI playService;

    @PostMapping("textInput")
    public Object playerText(PlayerInputVo inputVo) {
        return playService.inputCircleScore(inputVo);
    }

    @GetMapping("rank")
    public Object playerRank() {
        return playService.playerRank();
    }
}
