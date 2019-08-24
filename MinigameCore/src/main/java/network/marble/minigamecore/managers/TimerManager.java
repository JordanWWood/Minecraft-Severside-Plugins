package network.marble.minigamecore.managers;

import network.marble.dataaccesslayer.entities.timer.Timer;
import network.marble.dataaccesslayer.interfaces.ITimerManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/***
 * @deprecated Please use (@link network.marble.dataaccesslayer.managers.TimerManager)
 */
@Deprecated
public class TimerManager implements ITimerManager {
    protected static TimerManager instance;

    private network.marble.dataaccesslayer.managers.TimerManager i() {
        return network.marble.dataaccesslayer.managers.TimerManager.getInstance();
    }

    @Override
    public UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, TimeUnit everyUnit, long until, TimeUnit untilUnit) {
        return i().runEveryUntil(task, every, everyUnit, until, untilUnit);
    }

    @Override
    public UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, long until, TimeUnit untilUnit) {
        return i().runEveryUntil(task, every, until, untilUnit);
    }

    @Override
    public UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, TimeUnit everyUnit, long until) {
        return i().runEveryUntil(task, every, everyUnit, until);
    }

    @Override
    public UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, long until) {
        return i().runEveryUntil(task, every, until);
    }

    @Override
    public UUID runEvery(BiConsumer<Timer, Boolean> task, long every, TimeUnit unit) {
        return i().runEvery(task, every, unit);
    }

    @Override
    public UUID runEvery(BiConsumer<Timer, Boolean> task, long every) {
        return i().runEvery(task, every);
    }

    @Override
    public UUID runIn(BiConsumer<Timer, Boolean> task, long delay, TimeUnit unit) {
        return i().runIn(task, delay, unit);
    }

    @Override
    public UUID runIn(BiConsumer<Timer, Boolean> task, long delay) {
        return i().runIn(task, delay);
    }

    @Override
    public boolean stopTimer(UUID id) {
        return i().stopTimer(id);
    }

    @Override
    public boolean updateTimerTask(UUID id, BiConsumer<Timer, Boolean> task) {
        return i().updateTimerTask(id, task);
    }

    @Override
    public boolean updateTimerEvery(UUID id, long every, TimeUnit unit) {
        return i().updateTimerEvery(id, every, unit);
    }

    @Override
    public boolean updateTimerEvery(UUID id, long every) {
        return i().updateTimerEvery(id, every);
    }

    @Override
    public void cleanUp() {
        i().cleanUp();
    }

    public static TimerManager getInstance() {
        if (instance == null) instance = new TimerManager();
        return instance;
    }
}
