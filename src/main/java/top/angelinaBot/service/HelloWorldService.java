package top.angelinaBot.service;

import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.Angelina;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

@Service
public class HelloWorldService {

    @Angelina(keyWords = {"哈啰", "hello"})
    public ReplayInfo helloWorld(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo();
        replayInfo.setReplayMessage("Hello World！");
        return replayInfo;
    }

    @Angelina(keyWords = {"测试", "test"})
    public ReplayInfo test(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo();
        replayInfo.setReplayMessage("测试");
        return replayInfo;
    }

}
