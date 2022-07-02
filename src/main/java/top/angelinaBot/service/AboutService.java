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
        replayInfo.setReplayMessage("框架项目地址:https://github.com/Strelizia02/AngelinaFrame");
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"测试"})
    public ReplayInfo test(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        replayInfo.setMp3("D:/安洁莉娜_任命助理.mp3");
        return replayInfo;
    }
}
