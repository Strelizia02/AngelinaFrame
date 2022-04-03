package top.angelinaBot.service;

import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

@Service
public class HelloWorldService {

    @AngelinaGroup(keyWords = {"哈啰", "hello"})
    public ReplayInfo helloWorld(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo();
        replayInfo.setReplayMessage("Hello World！");
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"测试", "test"})
    public ReplayInfo test(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo();
        replayInfo.setReplayMessage("测试");
        return replayInfo;
    }

}
