package aphorea.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AphTimeout {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * @warning This method does not work with the game's ticks but with real-time. It must only be used in the client environment in non-important visual effects.
     */
    public static void setTimeout(Runnable task, int mills) {
        scheduler.schedule(task, mills, TimeUnit.MILLISECONDS);
    }

}