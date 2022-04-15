package top.angelinaBot.service;

import lombok.extern.slf4j.Slf4j;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayGroupInfo;

@Slf4j
public class TestService extends HelloWorldService{

    @AngelinaGroup(keyWords = {"哈啰", "hello"})
    public ReplayGroupInfo helloWorld(MessageInfo messageInfo) {
        log.info("Test extend HelloWorldService");
        return new ReplayGroupInfo(messageInfo);
    }

}
