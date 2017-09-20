package network.marble.vanity.managers;

import lombok.Getter;
import network.marble.vanity.Vanity;
import network.marble.vanity.api.type.base.VanityItemBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class VanityPluginManager {
    @Getter private Map<String, VanityItemBase> plugins;

    public VanityPluginManager() {
        plugins = new HashMap<>();

        init();
    }

    private void init() {
        Vanity.getInstance().getLogger().info("Loading VanityItems");
        File loc = Vanity.getInstance().getDataFolder();

        if (!loc.exists()) {
            Vanity.getInstance().getLogger().warning("Unable to find vanity folder ("+loc.getAbsolutePath()+"). Creating");

            loc.mkdir();
        }

        File[] flist = loc.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));

        if (flist == null) {
            Vanity.getInstance().getLogger().warning("Failed to find any vanity jars out of "+loc.listFiles().length+" files");
            return;
        }

        for (File f : flist) {
            VanityItemBase vp = loadVanity(f, Vanity.getInstance());
            if(vp != null) {
                try {
                    plugins.put(vp.getName(), vp);
                    Vanity.getInstance().getLogger().info("Loaded vanity item " + vp.getName());
                } catch(AbstractMethodError e){
                    Vanity.getInstance().getLogger().warning("Vanity item at \"" + f.getName() + "\" is out of date.");
                }
            }
        }
        Vanity.getInstance().getLogger().info("Loaded Vanity Items");
    }

    private VanityItemBase loadVanity(File file, Vanity plugin) {
        String infoFileName = "vanity.info";

        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> entries = jarFile.entries();
            String mainClass = null;
            while (entries.hasMoreElements()) {
                JarEntry element = entries.nextElement();
                if (element.getName().equalsIgnoreCase(infoFileName)) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
                    mainClass = reader.readLine().substring(5);
                    reader.close();
                    break;
                }
            }
            jarFile.close();
            if (mainClass != null) {
                ClassLoader loader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() }, plugin.getClass()
                        .getClassLoader());
                Class<?> classes = Class.forName(mainClass, true, loader);
                for (Class<?> subclasses : classes.getClasses()) {
                    Class.forName(subclasses.getName(), true, loader);
                }
                Class<? extends VanityItemBase> typeClass = classes.asSubclass(VanityItemBase.class);
                return typeClass.newInstance();
            } else {
                Vanity.getInstance().getLogger().warning("Failed to load " + file.getName() + ". Unable to locate "+infoFileName);
            }
        } catch (Exception e) {
            Vanity.getInstance().getLogger().severe("Failed to load vanity item: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
