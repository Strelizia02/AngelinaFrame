package top.angelinaBot.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import top.angelinaBot.service.CenterService;

public class CleanChatDataJob {

    @Autowired
    private CenterService centerService;


    /**
     * 每日总结一次，Send给中心
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24, initialDelay = -1)
    @Async
    public void exterminateJob() {
        centerService.pushData();
    }
}
