package com.play;

import com.play.dto.PlayerInputVo;
import com.play.service.PlayServiceI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zz
 * @date 2021/8/30 16:27
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlayApplication.class)
public class PlayerTest {

    @Autowired
    PlayServiceI playService;

    @Test
    public void testInput() {
        PlayerInputVo playerInputVo = new PlayerInputVo();
        playerInputVo.setInput("12jidoyanbhijhjihzdfwe");
        playerInputVo.setPlayer("test");
        playService.inputCircleScore(playerInputVo);
    }
}
