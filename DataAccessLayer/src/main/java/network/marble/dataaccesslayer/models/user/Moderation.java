package network.marble.dataaccesslayer.models.user;

public class Moderation {
    public boolean muted;

    public long ban_end_time;

    public long mute_end_time;

    public boolean banned;

    @Override
    public String toString() {
        return "Moderation{" +
                "muted=" + muted +
                ", ban_end_time=" + ban_end_time +
                ", mute_end_time=" + mute_end_time +
                ", banned=" + banned +
                '}';
    }
}
