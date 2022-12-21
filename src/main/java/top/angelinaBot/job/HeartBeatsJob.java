package top.angelinaBot.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import top.angelinaBot.service.CenterService;

public class HeartBeatsJob {

    @Autowired
    private CenterService centerService;

    @Scheduled(fixDelay = 3000, initialDelay = -1)
    @Async
    public void heartBeats() {
        centerService.heartBeats();
    }
}
