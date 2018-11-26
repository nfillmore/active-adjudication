package demo.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by cengruilin on 2017/8/11.
 */
@Component
public class SchedulingTasks {
    private static final Logger log = LoggerFactory.getLogger(SchedulingTasks.class);

    @Scheduled(fixedDelay = 5000)
    public void doSchedulingTaskInFixedDeplay() {
        log.info("doSchedulingTaskInFixedDeplay");
    }
}
