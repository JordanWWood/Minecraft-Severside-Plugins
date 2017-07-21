package network.marble.minigamecore.lock;

import network.marble.minigamecore.MiniGameCore;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class LockFile {
    public static String getFileLocation()
    {
        return MiniGameCore.instance.getDataFolder().getAbsolutePath()+"/minigamescore.lock";
    }

    public static File getFile()
    {
        return new File(getFileLocation());
    }

    public static boolean createLockFile(UUID uuid)
    {
        if (!checkLockFile()) try {
            boolean result = getFile().createNewFile();
            if (result) FileUtils.writeStringToFile(getFile(), uuid.toString());
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteLockFile()
    {
        return getFile().delete();
    }

    public static boolean checkLockFile()
    {
        return getFile().exists();
    }

    public static UUID getLockFileUUID(){
        try {
            List<String> lines = FileUtils.readLines(getFile());
            return lines.size() > 0 ? UUID.fromString(lines.get(0)) : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
