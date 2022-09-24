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
        replayInfo.setReplayMessage("框架主页:https://www.angelina-bot.top/");
        return replayInfo;
    }
}
