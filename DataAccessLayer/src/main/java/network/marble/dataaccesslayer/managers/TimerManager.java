package network.marble.dataaccesslayer.managers;

import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.entities.timer.Timer;
import network.marble.dataaccesslayer.entities.timer.TimerType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class TimerManager {
    private static TimerManager instance;

    private static Thread mainCounter = null;
    private static ConcurrentHashMap<UUID, Timer> timers = new ConcurrentHashMap<>();

    public TimerManager() {
        startMainThread();
    }

    private void startMainThread() {
        if (mainCounter == null) mainCounter = new Thread(() -> {
            for(;;) {
                Long currentTime = System.currentTimeMillis();
                List<UUID> toRemove = new ArrayList<>();
                timers.forEach((id, timer) -> {
                    if (timer.executeAt <= currentTime) {
                        final boolean last;
                        switch (timer.type) {
                            case SINGLE:
                                last = true;
                                toRemove.add(id);
                                break;
                            case FINITE:
                                timer.executeAt = currentTime + timer.executeEvery;
                                if (currentTime >= timer.executeUntil || timer.executeAt >= timer.executeUntil) {
                                    last = true;
                                    toRemove.add(id);
                                } else last = false;
                                break;
                            case INFINITE:
                                timer.executeAt = currentTime + timer.executeEvery;
                                last = false;
                                break;
                            default:
                                last = false;
                                break;
                        }
                        new Thread(() -> {
                            try {
                                timer.task.accept(timer, last);
                            } catch(Exception e) {
                                DataAccessLayer.instance.logger.severe("Exception in timer task");
                                e.printStackTrace();
                            }
                        }).start();
                    }
                });
                toRemove.forEach(timers::remove);
                try {
                    Thread.sleep(25L);
                } catch (InterruptedException e) {
                    DataAccessLayer.instance.logger.info("InterruptedException");
                }
            }
        });
        mainCounter.start();
    }

    public UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, TimeUnit everyUnit, long until, TimeUnit untilUnit) {
        return runEveryUntil(task, everyUnit.toMillis(every), untilUnit.toMillis(until));
    }

    public UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, long until, TimeUnit untilUnit) {
        return runEveryUntil(task, every, untilUnit.toMillis(until));
    }

    public UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, TimeUnit everyUnit, long until) {
        return runEveryUntil(task, everyUnit.toMillis(every), until);
    }

    public UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, long until) {
        Timer timer = new Timer(TimerType.FINITE, task);
        long current = System.currentTimeMillis();
        timer.executeAt = current + every;
        timer.executeEvery = every;
        timer.executeUntil = current + until + 1;
        timers.put(timer.id, timer);
        return  timer.id;
    }

    public UUID runEvery(BiConsumer<Timer, Boolean> task, long every, TimeUnit unit) {
        return runEvery(task, unit.toMillis(every));
    }

    public UUID runEvery(BiConsumer<Timer, Boolean> task, long every) {
        Timer timer = new Timer(TimerType.INFINITE, task);
        timer.executeAt = System.currentTimeMillis() + every;
        timer.executeEvery = every;
        timers.put(timer.id, timer);
        return  timer.id;
    }

    public UUID runIn(BiConsumer<Timer, Boolean> task, long delay, TimeUnit unit) {
        return runIn(task, unit.toMillis(delay));
    }

    public UUID runIn(BiConsumer<Timer, Boolean> task, long delay) {
        Timer timer = new Timer(TimerType.SINGLE, task);
        timer.executeAt = System.currentTimeMillis() + delay + 1;
        timers.put(timer.id, timer);
        return  timer.id;
    }

    public void stopTimer(UUID id) {
        if (id != null && timers.containsKey(id)) timers.remove(id);
    }

    public boolean updateTimerTask(UUID id, BiConsumer<Timer, Boolean> task) {
        if (timers.containsKey(id)) {
            Timer t = timers.remove(id);
            t.task = task;
            timers.put(id, t);
            return true;
        }
        return false;
    }

    /**
     * Warning: this will reset next execution time
     */
    public boolean updateTimerEvery(UUID id, long every, TimeUnit unit) {
        return updateTimerEvery(id, unit.toMillis(every));
    }

    /**
     * Warning: this will reset next execution time
     */
    public boolean updateTimerEvery(UUID id, long every) {
        if (timers.containsKey(id)) {
            Timer t = timers.remove(id);
            boolean check = t.type != TimerType.SINGLE;
            if (check) {
                t.executeEvery = every;
                t.executeAt = System.currentTimeMillis() + every;
            }
            timers.put(id, t);
            return check;
        }
        return false;
    }

    public void cleanUp() {
        timers.clear();
    }

    public static TimerManager getInstance() {
        if (instance == null) instance = new TimerManager();
        return instance;
    }
}
