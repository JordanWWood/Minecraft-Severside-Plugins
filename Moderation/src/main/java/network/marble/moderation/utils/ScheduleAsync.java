package network.marble.moderation.utils;

import net.md_5.bungee.api.ProxyServer;
import network.marble.moderation.Moderation;

import java.util.concurrent.TimeUnit;

public class ScheduleAsync {

    /**
     * Schedules a task and executes it asynchronously.
     *
     * @param runnable The Runnable that should be executed asynchronously after the specified time.
     * @param time     The amount of time the task should be scheduled.
     * @param timeUnit The {@link TimeUnit} of the time parameter.
     */
    public ScheduleAsync(final Runnable runnable, long time, TimeUnit timeUnit) {
        ProxyServer.getInstance().getScheduler().schedule(Moderation.getInstance(), new Runnable() {
            @Override
            public void run() {
                ProxyServer.getInstance().getScheduler().runAsync(Moderation.getInstance(), runnable);
            }
        }, time, timeUnit);
    }
}
