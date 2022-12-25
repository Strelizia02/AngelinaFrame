package top.angelinaBot.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.angelinaBot.service.CenterService;

@Component
public class HeartBeatsJob {

    @Autowired
    private CenterService centerService;

    @Scheduled(fixedDelay = 180000, initialDelay = -1)
    @Async
    public void heartBeats() {
        centerService.heartBeats();
    }
}
