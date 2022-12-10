package top.angelinaBot.util;

import top.angelinaBot.model.ReplayInfo;

public interface AngelinaSendMessageUtil {
    void sendGroupMsg(ReplayInfo replayInfo);

    void sendFriendMsg(ReplayInfo replayInfo);
}
