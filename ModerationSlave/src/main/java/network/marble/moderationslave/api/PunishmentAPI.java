package network.marble.moderationslave.api;


import lombok.Getter;
import network.marble.inventoryapi.interfaces.ActionExecutor;

import java.util.ArrayList;

public class PunishmentAPI {
    @Getter private static ArrayList<ActionExecutor> punishExecutions = new ArrayList<>();

    public static void registerPunishmentAction(ActionExecutor a){
        punishExecutions.add(a);
    }
}
