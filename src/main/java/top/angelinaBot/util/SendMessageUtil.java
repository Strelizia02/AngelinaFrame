package top.angelinaBot.util;

import top.angelinaBot.model.ReplayInfo;

import java.io.IOException;

public interface SendMessageUtil {
    void sendGroupMsg(ReplayInfo replayInfo);

    void sendFriendMsg(ReplayInfo replayInfo);

    void sendStrangerMsg(ReplayInfo replayInfo);

    void sendGroupTempMsg(ReplayInfo replayInfo);
}
