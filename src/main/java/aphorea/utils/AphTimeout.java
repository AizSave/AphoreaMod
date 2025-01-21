package aphorea.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AphTimeout {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void setTimeout(Runnable task, int mills) {
        scheduler.schedule(task, mills, TimeUnit.MILLISECONDS);
    }

}