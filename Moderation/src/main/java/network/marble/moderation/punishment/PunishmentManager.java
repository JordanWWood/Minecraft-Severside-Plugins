package network.marble.moderation.punishment;

import lombok.NoArgsConstructor;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import network.marble.moderation.Moderation;
import network.marble.moderation.punishment.communication.RabbitListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@NoArgsConstructor
public class PunishmentManager {
    private HashMap<UUID, PunishmentTask> punishmentTasks = new HashMap<>();
    private RabbitListener rabbitListener = new RabbitListener();

    /**
     * Checks if a UserConnection is still online.
     *
     * @param user The User that should be checked.
     * @return true, if the UserConnection is still online.
     */
    public boolean isUserOnline(UserConnection user) {
        return Moderation.getInstance().getProxy().getPlayer(user.getUniqueId()) != null;
    }

    /**
     * Reconnects a User to a Server, as long as the user is currently online. If he isn't, his limbo task (if he had one)
     * will be canceled.
     *
     * @param user   The User that should be reconnected.
     * @param server The Server the User should be connected to.
     */
    public void limboIfOnline(UserConnection user, ServerConnection server) {
        if (isUserOnline(user)) {
            if (!isUnderPunishment(user.getUniqueId())) {
                limbo(user, server);
            }
        } else {
            cancelPunishmentTask(user.getUniqueId());
        }
    }

    /**
     * Reconnects the User without checking whether he's online or not.
     *
     * @param user   The User that should be reconnected.
     * @param server The Server the User should be connected to.
     */
    private void limbo(UserConnection user, ServerConnection server) {
        try {
            rabbitListener.DeclareQueueForPlayer(user.getUniqueId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        PunishmentTask punishmentTask = punishmentTasks.computeIfAbsent(user.getUniqueId(), k -> new PunishmentTask(Moderation.getInstance().getProxy(), user, server));
        punishmentTask.tick();
    }

    /**
     * Removes a limbo task from the main HashMap
     *
     * @param uuid The UniqueId of the User.
     */
    void cancelPunishmentTask(UUID uuid) {
        PunishmentTask task = punishmentTasks.remove(uuid);
    }



    /**
     * Checks whether a User has got a punishment task.
     *
     * @param uuid The UniqueId of the User.
     * @return true, if there is a task that tries to limbo the User to a server.
     */
    public boolean isUnderPunishment(UUID uuid) {
        return punishmentTasks.containsKey(uuid);
    }

    public int getKeepAliveMillis() {
        return 1000;
    }

    public void sendMessageToPlayer(UUID uuid, String message) {
        punishmentTasks.get(uuid).getMessages().add(message);
    }
}
