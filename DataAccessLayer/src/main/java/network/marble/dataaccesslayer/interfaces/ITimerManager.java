package network.marble.dataaccesslayer.interfaces;

import network.marble.dataaccesslayer.entities.timer.Timer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public interface ITimerManager {
    UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, TimeUnit everyUnit, long until, TimeUnit untilUnit);

    UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, long until, TimeUnit untilUnit);

    UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, TimeUnit everyUnit, long until);

    UUID runEveryUntil(BiConsumer<Timer, Boolean> task, long every, long until);

    UUID runEvery(BiConsumer<Timer, Boolean> task, long every, TimeUnit unit);

    UUID runEvery(BiConsumer<Timer, Boolean> task, long every);

    UUID runIn(BiConsumer<Timer, Boolean> task, long delay, TimeUnit unit);

    UUID runIn(BiConsumer<Timer, Boolean> task, long delay);

    boolean stopTimer(UUID id);

    boolean updateTimerTask(UUID id, BiConsumer<Timer, Boolean> task);

    boolean updateTimerEvery(UUID id, long every, TimeUnit unit);

    boolean updateTimerEvery(UUID id, long every);

    void cleanUp();
}
