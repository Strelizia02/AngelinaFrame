package top.angelinaBot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangzy
 * @Date 2020/12/24 15:58
 * 私聊回复接口单独实现，尽量不与群聊接口重复
 **/
@RequestMapping("PrivateChat")
@RestController
@Slf4j
public class PrivateChatController {

}
