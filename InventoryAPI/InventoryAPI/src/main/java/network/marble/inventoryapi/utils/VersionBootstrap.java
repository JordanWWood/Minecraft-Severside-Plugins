package network.marble.inventoryapi.utils;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public class VersionBootstrap {
    @Getter
    private static String version;

    static {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        version = packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    /**
     * Gets class from the correct version based on the objectName provided and attempts to return it as the provided type
     *
     * @param type The class the found object should extend
     * @param objectName The object to find within the discovered version
     * @return An instance of the found class or null if none is found or the class is not assignable to type
     */
    public static <T> T getClassFromType(Class<?> type, String objectName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Class<?> clazz = Class.forName("network.marble.inventoryapi.impl." + version + "." + objectName);
        if (type.isAssignableFrom(clazz))
            return (T) clazz.getConstructor().newInstance();

        return null;
    }
}
