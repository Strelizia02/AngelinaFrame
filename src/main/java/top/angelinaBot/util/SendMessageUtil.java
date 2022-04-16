package top.angelinaBot.util;

import top.angelinaBot.model.ReplayInfo;

public interface SendMessageUtil {
    void sendGroupMsg(ReplayInfo replayInfo);

    void sendFriendMsg(ReplayInfo replayInfo);
}
