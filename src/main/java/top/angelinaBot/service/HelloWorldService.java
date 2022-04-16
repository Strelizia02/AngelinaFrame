package top.angelinaBot.service;

import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaEvent;
import top.angelinaBot.annotation.AngelinaFriend;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.EventEnum;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

@Service("helloWorldService")
public class HelloWorldService {

    @AngelinaGroup(keyWords = {"哈啰", "hello"})
    public ReplayInfo helloWorld(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        replayInfo.setReplayMessage("Hello World！");
        new HelloWorldService().test(messageInfo);
        return replayInfo;
    }

    @AngelinaEvent(event = EventEnum.NudgeEvent)
    @AngelinaFriend(keyWords = {"test", "测试"})
    @AngelinaGroup(keyWords = {"测试", "test"})
    public ReplayInfo test(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        replayInfo.setReplayMessage("测试");
        return replayInfo;
    }

}
