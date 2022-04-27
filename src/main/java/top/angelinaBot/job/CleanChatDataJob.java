package top.angelinaBot.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import top.angelinaBot.dao.ActivityMapper;

public class CleanChatDataJob {

    @Autowired
    private ActivityMapper activityMapper;

    @Scheduled(cron = "0 0 4 */1 * ?")
    @Async
    public void exterminateJob() {
        activityMapper.clearActivity();
    }
}
