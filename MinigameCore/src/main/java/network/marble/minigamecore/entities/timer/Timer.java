package network.marble.minigamecore.entities.timer;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Timer {
    public final UUID id;
    public BiConsumer<Timer, Boolean> task;
    public long executeAt;
    public final TimerType type;
    public long executeEvery;
    public long executeUntil;

    public Timer(TimerType type, BiConsumer<Timer, Boolean> task) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.task = task;
    }
}
