package top.angelinaBot.service;

import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

@Service
public class AboutService {

    @AngelinaGroup(keyWords = {"关于"}, description = "框架地址源码")
    public ReplayInfo getAbout(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        replayInfo.setReplayMessage(new String(new byte[]{-26, -95, -122, -26, -98, -74, -28, -72, -69, -23, -95, -75, 58, 104, 116, 116, 112, 115, 58, 47, 47, 119, 119, 119, 46, 97, 110, 103, 101, 108, 105, 110, 97, 45, 98, 111, 116, 46, 116, 111, 112, 47}));
        return replayInfo;
    }
}
