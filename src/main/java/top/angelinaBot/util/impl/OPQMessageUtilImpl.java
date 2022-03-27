package top.angelinaBot.util.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.SendMessageUtil;

@Component("opq")
@Slf4j
public class OPQMessageUtilImpl implements SendMessageUtil {
    @Override
    public void sendGroupMsg(ReplayInfo replayInfo) {
        //具体的OPQ发消息方法
        log.info("发送消息" + replayInfo.getReplayMessage());
    }
}
