package top.angelinaBot.service;

import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayGroupInfo;

@Service
public class HelloWorldService {

    @AngelinaGroup(keyWords = {"哈啰", "hello"})
    public ReplayGroupInfo helloWorld(MessageInfo messageInfo) {
        ReplayGroupInfo replayInfo = new ReplayGroupInfo(messageInfo);
        replayInfo.setReplayMessage("Hello World！");
        new HelloWorldService().test(messageInfo);
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"测试", "test"})
    public ReplayGroupInfo test(MessageInfo messageInfo) {
        ReplayGroupInfo replayInfo = new ReplayGroupInfo(messageInfo);
        replayInfo.setReplayMessage("测试");
        return replayInfo;
    }

}
