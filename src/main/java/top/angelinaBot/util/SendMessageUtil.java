package top.angelinaBot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.angelinaBot.container.QQFrameContainer;
import top.angelinaBot.model.ReplayInfo;

import java.io.IOException;

import static top.angelinaBot.container.QQFrameContainer.QQChannel;

@Component
public class SendMessageUtil {

    @Autowired
    private QQFrameContainer qqFrameContainer;

    public void sendGroupMsg(ReplayInfo replayInfo) {
        AngelinaSendMessageUtil sendMessageUtil = qqFrameContainer.qqFrameMap.get(replayInfo.getFrame());
        sendMessageUtil.sendGroupMsg(replayInfo);
    }

    public void sendFriendMsg(ReplayInfo replayInfo) {
        AngelinaSendMessageUtil sendMessageUtil = qqFrameContainer.qqFrameMap.get(replayInfo.getFrame());
        sendMessageUtil.sendGroupMsg(replayInfo);
    }
}
